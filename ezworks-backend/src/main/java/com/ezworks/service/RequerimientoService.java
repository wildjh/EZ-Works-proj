package com.ezworks.service;

import com.ezworks.domain.enums.EstadoPostulacion;
import com.ezworks.domain.enums.EstadoRequerimiento;
import com.ezworks.domain.enums.RolCodigo;
import com.ezworks.domain.job.*;
import com.ezworks.domain.messaging.Conversacion;
import com.ezworks.domain.user.PerfilAyudante;
import com.ezworks.domain.user.PerfilEmpleador;
import com.ezworks.dto.job.*;
import com.ezworks.exception.ApiException;
import com.ezworks.repository.*;
import com.ezworks.security.UserPrincipal;
import com.ezworks.util.Mapper;
import com.ezworks.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequerimientoService {

    private final RequerimientoRepository requerimientoRepository;
    private final CategoriaRepository categoriaRepository;
    private final PerfilEmpleadorRepository perfilEmpleadorRepository;
    private final PerfilAyudanteRepository perfilAyudanteRepository;
    private final PostulacionRepository postulacionRepository;
    private final EmparejamientoRepository emparejamientoRepository;
    private final ConversacionRepository conversacionRepository;

    @Transactional
    public RequerimientoResponse crear(RequerimientoRequest req) {
        PerfilEmpleador empleador = requireEmpleador();
        Categoria cat = categoriaRepository.findById(req.getCategoriaId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));

        Requerimiento r = Requerimiento.builder()
                .empleador(empleador)
                .categoria(cat)
                .titulo(req.getTitulo())
                .descripcion(req.getDescripcion())
                .remuneracion(req.getRemuneracion())
                .estado(EstadoRequerimiento.BORRADOR)
                .zonaAproximada(req.getZonaAproximada())
                .latitudAprox(req.getLatitudAprox())
                .longitudAprox(req.getLongitudAprox())
                .build();

        r = requerimientoRepository.save(r);
        return Mapper.toRequerimiento(r, null);
    }

    @Transactional
    public RequerimientoResponse actualizar(Long id, RequerimientoRequest req) {
        PerfilEmpleador empleador = requireEmpleador();
        Requerimiento r = loadOwnedRequerimiento(id, empleador.getId());

        if (r.getEstado() != EstadoRequerimiento.BORRADOR && r.getEstado() != EstadoRequerimiento.PUBLICADO) {
            throw new ApiException(HttpStatus.CONFLICT, "No se puede editar en el estado actual");
        }
        if (emparejamientoRepository.existsByRequerimientoId(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya tiene un match; no se puede editar");
        }

        Categoria cat = categoriaRepository.findById(req.getCategoriaId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));

        r.setCategoria(cat);
        r.setTitulo(req.getTitulo());
        r.setDescripcion(req.getDescripcion());
        r.setRemuneracion(req.getRemuneracion());
        r.setZonaAproximada(req.getZonaAproximada());
        r.setLatitudAprox(req.getLatitudAprox());
        r.setLongitudAprox(req.getLongitudAprox());

        return Mapper.toRequerimiento(requerimientoRepository.save(r), null);
    }

    @Transactional
    public RequerimientoResponse publicar(Long id) {
        PerfilEmpleador empleador = requireEmpleador();
        Requerimiento r = loadOwnedRequerimiento(id, empleador.getId());
        if (r.getEstado() != EstadoRequerimiento.BORRADOR) {
            throw new ApiException(HttpStatus.CONFLICT, "Solo se publican requerimientos en borrador");
        }
        r.setEstado(EstadoRequerimiento.PUBLICADO);
        r.setPublicadoEn(Instant.now());
        return Mapper.toRequerimiento(requerimientoRepository.save(r), null);
    }

    @Transactional(readOnly = true)
    public List<RequerimientoResponse> misRequerimientos() {
        PerfilEmpleador empleador = requireEmpleador();
        return requerimientoRepository.findByEmpleadorIdOrderByActualizadoEnDesc(empleador.getId()).stream()
                .map(r -> Mapper.toRequerimiento(r, emparejamientoIdOf(r.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RequerimientoResponse> vacantesPublicadas() {
        return requerimientoRepository.findByEstadoOrderByPublicadoEnDesc(EstadoRequerimiento.PUBLICADO).stream()
                .map(r -> Mapper.toRequerimiento(r, null))
                .toList();
    }

    @Transactional(readOnly = true)
    public RequerimientoResponse obtener(Long id) {
        Requerimiento r = requerimientoRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Requerimiento no encontrado"));
        return Mapper.toRequerimiento(r, emparejamientoIdOf(id));
    }

    @Transactional
    public PostulacionResponse postular(Long requerimientoId, PostulacionRequest req) {
        PerfilAyudante ayudante = requireAyudante();
        Requerimiento r = requerimientoRepository.findById(requerimientoId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Requerimiento no encontrado"));

        if (r.getEstado() != EstadoRequerimiento.PUBLICADO) {
            throw new ApiException(HttpStatus.CONFLICT, "El requerimiento no acepta postulaciones");
        }
        if (postulacionRepository.existsByRequerimientoIdAndAyudanteId(requerimientoId, ayudante.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya postuló a esta vacante");
        }

        Postulacion p = postulacionRepository.save(Postulacion.builder()
                .requerimiento(r)
                .ayudante(ayudante)
                .mensajePresentacion(req.getMensajePresentacion())
                .estado(EstadoPostulacion.PENDIENTE)
                .build());

        Postulacion saved = postulacionRepository.findByIdWithAyudante(p.getId()).orElse(p);
        return Mapper.toPostulacion(saved);
    }

    @Transactional(readOnly = true)
    public List<PostulacionResponse> listarPostulaciones(Long requerimientoId) {
        PerfilEmpleador empleador = requireEmpleador();
        loadOwnedRequerimiento(requerimientoId, empleador.getId());
        return postulacionRepository.findByRequerimientoIdWithAyudante(requerimientoId).stream()
                .map(Mapper::toPostulacion)
                .toList();
    }

    @Transactional
    public EmparejamientoResponse crearMatch(Long requerimientoId, MatchRequest req) {
        PerfilEmpleador empleador = requireEmpleador();
        Requerimiento r = loadOwnedRequerimiento(requerimientoId, empleador.getId());

        if (r.getEstado() != EstadoRequerimiento.PUBLICADO) {
            throw new ApiException(HttpStatus.CONFLICT, "El requerimiento debe estar publicado");
        }
        if (emparejamientoRepository.existsByRequerimientoId(requerimientoId)) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe un match para esta vacante");
        }

        Postulacion postulacion = postulacionRepository.findByIdAndRequerimientoId(req.getPostulacionId(), requerimientoId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Postulación no encontrada"));

        if (postulacion.getEstado() != EstadoPostulacion.PENDIENTE) {
            throw new ApiException(HttpStatus.CONFLICT, "La postulación no está pendiente");
        }

        postulacion.setEstado(EstadoPostulacion.ACEPTADA);
        postulacionRepository.save(postulacion);

        postulacionRepository.findByRequerimientoIdOrderByCreadoEnDesc(requerimientoId).stream()
                .filter(p -> !p.getId().equals(postulacion.getId()) && p.getEstado() == EstadoPostulacion.PENDIENTE)
                .forEach(p -> {
                    p.setEstado(EstadoPostulacion.RECHAZADA);
                    postulacionRepository.save(p);
                });

        r.setEstado(EstadoRequerimiento.EN_MATCH);

        Emparejamiento match = emparejamientoRepository.save(Emparejamiento.builder()
                .requerimiento(r)
                .empleador(empleador)
                .ayudante(postulacion.getAyudante())
                .postulacion(postulacion)
                .build());

        Conversacion conv = conversacionRepository.save(Conversacion.builder()
                .emparejamiento(match)
                .build());

        requerimientoRepository.save(r);

        return EmparejamientoResponse.builder()
                .id(match.getId())
                .requerimientoId(requerimientoId)
                .postulacionId(postulacion.getId())
                .conversacionId(conv.getId())
                .establecidoEn(match.getEstablecidoEn())
                .build();
    }

    private Long emparejamientoIdOf(Long requerimientoId) {
        return emparejamientoRepository.findByRequerimientoId(requerimientoId)
                .map(Emparejamiento::getId)
                .orElse(null);
    }

    private Requerimiento loadOwnedRequerimiento(Long id, Long empleadorId) {
        Requerimiento r = requerimientoRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Requerimiento no encontrado"));
        if (!r.getEmpleador().getId().equals(empleadorId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No es dueño de este requerimiento");
        }
        return r;
    }

    private PerfilEmpleador requireEmpleador() {
        UserPrincipal p = SecurityUtils.currentUser();
        if (!p.hasRole(RolCodigo.EMPLEADOR)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Se requiere rol Empleador");
        }
        return perfilEmpleadorRepository.findByUsuarioId(p.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Perfil empleador no configurado"));
    }

    private PerfilAyudante requireAyudante() {
        UserPrincipal p = SecurityUtils.currentUser();
        if (!p.hasRole(RolCodigo.AYUDANTE)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Se requiere rol Ayudante");
        }
        return perfilAyudanteRepository.findByUsuarioId(p.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Perfil ayudante no configurado"));
    }
}
