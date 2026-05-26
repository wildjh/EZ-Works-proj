package com.ezworks.service;

import com.ezworks.domain.enums.RolCodigo;
import com.ezworks.domain.user.*;
import com.ezworks.dto.user.EvidenciaResponse;
import com.ezworks.dto.user.PerfilAyudantePublicoResponse;
import com.ezworks.dto.user.PerfilEmpleadorPublicoResponse;
import com.ezworks.exception.ApiException;
import com.ezworks.repository.EvidenciaTrabajoRepository;
import com.ezworks.repository.PerfilAyudanteRepository;
import com.ezworks.repository.PerfilEmpleadorRepository;
import com.ezworks.util.Mapper;
import com.ezworks.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PerfilPublicoService {

    private final PerfilAyudanteRepository perfilAyudanteRepository;
    private final PerfilEmpleadorRepository perfilEmpleadorRepository;
    private final EvidenciaTrabajoRepository evidenciaTrabajoRepository;

    @Transactional(readOnly = true)
    public PerfilAyudantePublicoResponse obtenerPerfilAyudante(Long perfilAyudanteId) {
        if (!SecurityUtils.currentUser().hasRole(RolCodigo.EMPLEADOR)
                && !SecurityUtils.currentUser().hasRole(RolCodigo.ADMIN)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No autorizado para ver este perfil");
        }

        PerfilAyudante pa = perfilAyudanteRepository.findByIdWithUsuario(perfilAyudanteId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Perfil no encontrado"));

        Usuario u = pa.getUsuario();
        var evidencias = evidenciaTrabajoRepository.findByPerfilAyudanteIdOrderBySubidoEnDesc(pa.getId()).stream()
                .map(Mapper::toEvidencia)
                .map(this::toPublica)
                .toList();

        return PerfilAyudantePublicoResponse.builder()
                .id(pa.getId())
                .usuarioId(u.getId())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .bio(pa.getBio())
                .fotoPerfilUrl(u.getFotoPerfilUrl())
                .calificacionPromedio(pa.getCalificacionPromedio())
                .totalResenas(pa.getTotalResenas())
                .evidencias(evidencias)
                .build();
    }

    @Transactional(readOnly = true)
    public PerfilEmpleadorPublicoResponse obtenerPerfilEmpleador(Long perfilEmpleadorId) {
        if (!SecurityUtils.currentUser().hasRole(RolCodigo.AYUDANTE)
                && !SecurityUtils.currentUser().hasRole(RolCodigo.ADMIN)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No autorizado para ver este perfil");
        }

        PerfilEmpleador pe = perfilEmpleadorRepository.findByIdWithUsuario(perfilEmpleadorId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Perfil no encontrado"));

        Usuario u = pe.getUsuario();
        return PerfilEmpleadorPublicoResponse.builder()
                .id(pe.getId())
                .usuarioId(u.getId())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .fotoPerfilUrl(u.getFotoPerfilUrl())
                .calificacionPromedio(pe.getCalificacionPromedio())
                .totalResenas(pe.getTotalResenas())
                .build();
    }

    private PerfilAyudantePublicoResponse.EvidenciaPublica toPublica(EvidenciaResponse e) {
        return PerfilAyudantePublicoResponse.EvidenciaPublica.builder()
                .id(e.getId())
                .urlArchivo(e.getUrlArchivo())
                .tipo(e.getTipo())
                .descripcion(e.getDescripcion())
                .subidoEn(e.getSubidoEn())
                .build();
    }
}
