package com.ezworks.service;

import com.ezworks.domain.enums.EstadoCuenta;
import com.ezworks.domain.enums.RolCodigo;
import com.ezworks.domain.enums.TipoAccionAdmin;
import com.ezworks.domain.user.*;
import com.ezworks.dto.admin.ActualizarEstadoCuentaRequest;
import com.ezworks.dto.admin.AdminUsuarioResponse;
import com.ezworks.exception.ApiException;
import com.ezworks.repository.*;
import com.ezworks.security.UserPrincipal;
import com.ezworks.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final AccionAdminRepository accionAdminRepository;

    @Transactional(readOnly = true)
    public List<AdminUsuarioResponse> listarUsuarios() {
        requireAdmin();
        return usuarioRepository.findAllWithRoles().stream()
                .map(this::toAdminUsuario)
                .toList();
    }

    @Transactional
    public AdminUsuarioResponse actualizarEstado(Long usuarioId, ActualizarEstadoCuentaRequest req) {
        UserPrincipal admin = requireAdmin();
        if (admin.getId().equals(usuarioId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No puede modificar su propia cuenta");
        }

        Usuario usuario = usuarioRepository.findByIdWithRoles(usuarioId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        boolean esAdmin = usuario.getRoles().stream()
                .anyMatch(ur -> ur.getRol().getCodigo() == RolCodigo.ADMIN);
        if (esAdmin) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No puede modificar a otro administrador");
        }

        EstadoCuenta anterior = usuario.getEstadoCuenta();
        usuario.setEstadoCuenta(req.getEstadoCuenta());
        usuarioRepository.save(usuario);

        Usuario adminEntity = usuarioRepository.findById(admin.getId()).orElseThrow();
        accionAdminRepository.save(AccionAdmin.builder()
                .admin(adminEntity)
                .usuarioAfectado(usuario)
                .tipo(mapAccion(req.getEstadoCuenta(), anterior))
                .motivo(req.getMotivo())
                .build());

        return toAdminUsuario(usuario);
    }

    private TipoAccionAdmin mapAccion(EstadoCuenta nuevo, EstadoCuenta anterior) {
        if (nuevo == EstadoCuenta.ACTIVO && anterior != EstadoCuenta.ACTIVO) {
            return TipoAccionAdmin.HABILITAR;
        }
        if (nuevo == EstadoCuenta.SUSPENDIDO) {
            return TipoAccionAdmin.SUSPENSION;
        }
        if (nuevo == EstadoCuenta.BANEADO) {
            return TipoAccionAdmin.BAN;
        }
        return TipoAccionAdmin.ADVERTENCIA;
    }

    private AdminUsuarioResponse toAdminUsuario(Usuario u) {
        return AdminUsuarioResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .telefono(u.getTelefono())
                .fotoPerfilUrl(u.getFotoPerfilUrl())
                .estadoCuenta(u.getEstadoCuenta())
                .roles(u.getRoles().stream()
                        .map(ur -> ur.getRol().getCodigo())
                        .collect(Collectors.toList()))
                .creadoEn(u.getCreadoEn())
                .build();
    }

    private UserPrincipal requireAdmin() {
        UserPrincipal principal = SecurityUtils.currentUser();
        if (!principal.hasRole(RolCodigo.ADMIN)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Se requiere rol Administrador");
        }
        return principal;
    }
}
