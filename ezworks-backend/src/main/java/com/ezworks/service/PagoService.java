package com.ezworks.service;

import com.ezworks.domain.enums.EstadoCuenta;
import com.ezworks.domain.enums.EstadoDeuda;
import com.ezworks.domain.enums.EstadoTransaccion;
import com.ezworks.domain.enums.TipoTransaccion;
import com.ezworks.domain.job.Emparejamiento;
import com.ezworks.domain.payment.DeudaPlataforma;
import com.ezworks.domain.payment.MetodoPago;
import com.ezworks.domain.payment.Transaccion;
import com.ezworks.domain.user.Usuario;
import com.ezworks.dto.payment.*;
import com.ezworks.exception.ApiException;
import com.ezworks.repository.*;
import com.ezworks.security.UserPrincipal;
import com.ezworks.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagoService {

    private static final String CLAVE_COMISION = "comision_por_match";
    private static final String CLAVE_LIMITE = "limite_deuda_maxima";

    private final UsuarioRepository usuarioRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final DeudaPlataformaRepository deudaPlataformaRepository;
    private final TransaccionRepository transaccionRepository;
    private final ConfiguracionPlataformaRepository configuracionPlataformaRepository;

    @Transactional(readOnly = true)
    public MiDeudaResponse obtenerMiDeuda() {
        Usuario usuario = requireCurrentUsuario();
        BigDecimal limite = configDecimal(CLAVE_LIMITE, new BigDecimal("50000"));

        List<DeudaPlataforma> todas = deudaPlataformaRepository
                .findByUsuarioIdWithDetailsOrderByCreadoEnDesc(usuario.getId());
        List<DeudaResponse> pendientes = todas.stream()
                .filter(d -> d.getEstado() == EstadoDeuda.PENDIENTE)
                .map(this::toDeudaResponse)
                .toList();
        List<DeudaResponse> historial = todas.stream()
                .filter(d -> d.getEstado() != EstadoDeuda.PENDIENTE)
                .map(this::toDeudaResponse)
                .toList();
        List<TransaccionResponse> transacciones = transaccionRepository
                .findByUsuarioIdOrderByCreadoEnDesc(usuario.getId()).stream()
                .limit(10)
                .map(this::toTransaccionResponse)
                .toList();

        return MiDeudaResponse.builder()
                .saldoDeudaAcumulado(usuario.getSaldoDeudaAcumulado())
                .limiteDeudaMaxima(limite)
                .inhabilitadoPorDeuda(usuario.getEstadoCuenta() == EstadoCuenta.INHABILITADO_DEUDA)
                .deudasPendientes(pendientes)
                .historialDeudas(historial)
                .transaccionesRecientes(transacciones)
                .build();
    }

    @Transactional(readOnly = true)
    public List<MetodoPagoResponse> listarMetodosPago() {
        Usuario usuario = requireCurrentUsuario();
        return metodoPagoRepository.findByUsuarioIdAndActivoTrueOrderByPredeterminadoDescCreadoEnDesc(usuario.getId())
                .stream()
                .map(this::toMetodoPagoResponse)
                .toList();
    }

    @Transactional
    public MetodoPagoResponse registrarMetodoPago(MetodoPagoRequest request) {
        Usuario usuario = requireCurrentUsuario();

        if (request.isPredeterminado()) {
            metodoPagoRepository.findByUsuarioIdAndActivoTrueOrderByPredeterminadoDescCreadoEnDesc(usuario.getId())
                    .forEach(m -> {
                        m.setPredeterminado(false);
                        metodoPagoRepository.save(m);
                    });
        }

        MetodoPago metodo = metodoPagoRepository.save(MetodoPago.builder()
                .usuario(usuario)
                .tipo(request.getTipo())
                .alias(request.getAlias().trim())
                .ultimosCuatro(request.getUltimosCuatro())
                .predeterminado(request.isPredeterminado())
                .build());

        return toMetodoPagoResponse(metodo);
    }

    @Transactional
    public TransaccionResponse pagarDeuda(Long deudaId, Long metodoPagoId) {
        Usuario usuario = requireCurrentUsuario();
        DeudaPlataforma deuda = deudaPlataformaRepository.findByIdAndUsuarioIdWithDetails(deudaId, usuario.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Deuda no encontrada"));

        if (deuda.getEstado() != EstadoDeuda.PENDIENTE) {
            throw new ApiException(HttpStatus.CONFLICT, "La deuda ya fue pagada o no está pendiente");
        }

        MetodoPago metodo = metodoPagoRepository.findByIdAndUsuarioId(metodoPagoId, usuario.getId())
                .filter(MetodoPago::isActivo)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Método de pago no encontrado"));

        Instant now = Instant.now();
        Transaccion tx = transaccionRepository.save(Transaccion.builder()
                .usuario(usuario)
                .deuda(deuda)
                .metodoPago(metodo)
                .tipo(TipoTransaccion.PAGO_DEUDA)
                .monto(deuda.getMonto())
                .estado(EstadoTransaccion.COMPLETADA)
                .referencia("SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .completadaEn(now)
                .build());

        deuda.setEstado(EstadoDeuda.PAGADA);
        deuda.setPagadaEn(now);
        deudaPlataformaRepository.save(deuda);

        BigDecimal nuevoSaldo = usuario.getSaldoDeudaAcumulado().subtract(deuda.getMonto());
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            nuevoSaldo = BigDecimal.ZERO;
        }
        usuario.setSaldoDeudaAcumulado(nuevoSaldo);
        actualizarEstadoPorDeuda(usuario, configDecimal(CLAVE_LIMITE, new BigDecimal("50000")));
        usuarioRepository.save(usuario);

        return toTransaccionResponse(tx);
    }

    @Transactional
    public void registrarDeudaPorMatch(Emparejamiento match) {
        if (deudaPlataformaRepository.existsByEmparejamientoId(match.getId())) {
            return;
        }

        BigDecimal comision = configDecimal(CLAVE_COMISION, new BigDecimal("5000"));
        Usuario empleadorUsuario = match.getEmpleador().getUsuario();

        DeudaPlataforma deuda = deudaPlataformaRepository.save(DeudaPlataforma.builder()
                .usuario(empleadorUsuario)
                .emparejamiento(match)
                .monto(comision)
                .descripcion("Comisión por match — " + match.getRequerimiento().getTitulo())
                .build());

        empleadorUsuario.setSaldoDeudaAcumulado(
                empleadorUsuario.getSaldoDeudaAcumulado().add(comision));
        actualizarEstadoPorDeuda(empleadorUsuario, configDecimal(CLAVE_LIMITE, new BigDecimal("50000")));
        usuarioRepository.save(empleadorUsuario);

        transaccionRepository.save(Transaccion.builder()
                .usuario(empleadorUsuario)
                .deuda(deuda)
                .tipo(TipoTransaccion.COMISION_MATCH)
                .monto(comision)
                .estado(EstadoTransaccion.PENDIENTE)
                .referencia("MATCH-" + match.getId())
                .build());
    }

    private void actualizarEstadoPorDeuda(Usuario usuario, BigDecimal limite) {
        if (usuario.getSaldoDeudaAcumulado().compareTo(limite) > 0) {
            usuario.setEstadoCuenta(EstadoCuenta.INHABILITADO_DEUDA);
        } else if (usuario.getEstadoCuenta() == EstadoCuenta.INHABILITADO_DEUDA) {
            usuario.setEstadoCuenta(EstadoCuenta.ACTIVO);
        }
    }

    private BigDecimal configDecimal(String clave, BigDecimal defaultValue) {
        return configuracionPlataformaRepository.findById(clave)
                .map(c -> c.getValor())
                .orElse(defaultValue);
    }

    private Usuario requireCurrentUsuario() {
        UserPrincipal principal = SecurityUtils.currentUser();
        return usuarioRepository.findById(principal.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private DeudaResponse toDeudaResponse(DeudaPlataforma d) {
        String titulo = d.getEmparejamiento() != null && d.getEmparejamiento().getRequerimiento() != null
                ? d.getEmparejamiento().getRequerimiento().getTitulo()
                : null;
        return DeudaResponse.builder()
                .id(d.getId())
                .monto(d.getMonto())
                .estado(d.getEstado())
                .descripcion(d.getDescripcion())
                .requerimientoTitulo(titulo)
                .creadoEn(d.getCreadoEn())
                .pagadaEn(d.getPagadaEn())
                .build();
    }

    private TransaccionResponse toTransaccionResponse(Transaccion t) {
        return TransaccionResponse.builder()
                .id(t.getId())
                .tipo(t.getTipo())
                .monto(t.getMonto())
                .estado(t.getEstado())
                .referencia(t.getReferencia())
                .creadoEn(t.getCreadoEn())
                .completadaEn(t.getCompletadaEn())
                .build();
    }

    private MetodoPagoResponse toMetodoPagoResponse(MetodoPago m) {
        return MetodoPagoResponse.builder()
                .id(m.getId())
                .tipo(m.getTipo())
                .alias(m.getAlias())
                .ultimosCuatro(m.getUltimosCuatro())
                .predeterminado(m.isPredeterminado())
                .creadoEn(m.getCreadoEn())
                .build();
    }
}
