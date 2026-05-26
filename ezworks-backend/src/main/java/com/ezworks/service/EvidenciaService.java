package com.ezworks.service;

import com.ezworks.domain.enums.TipoEvidencia;
import com.ezworks.domain.user.EvidenciaTrabajo;
import com.ezworks.domain.user.PerfilAyudante;
import com.ezworks.dto.user.EvidenciaResponse;
import com.ezworks.exception.ApiException;
import com.ezworks.repository.EvidenciaTrabajoRepository;
import com.ezworks.repository.PerfilAyudanteRepository;
import com.ezworks.security.UserPrincipal;
import com.ezworks.util.Mapper;
import com.ezworks.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvidenciaService {

    private final EvidenciaTrabajoRepository evidenciaRepository;
    private final PerfilAyudanteRepository perfilAyudanteRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public List<EvidenciaResponse> listarMisEvidencias() {
        PerfilAyudante perfil = perfilAyudanteActual();
        return evidenciaRepository.findByPerfilAyudanteIdOrderBySubidoEnDesc(perfil.getId()).stream()
                .map(Mapper::toEvidencia)
                .toList();
    }

    @Transactional
    public EvidenciaResponse subir(MultipartFile archivo, String descripcion, TipoEvidencia tipo) {
        PerfilAyudante perfil = perfilAyudanteActual();
        String url = fileStorageService.storeImage(archivo, "evidencias");
        EvidenciaTrabajo evidencia = evidenciaRepository.save(EvidenciaTrabajo.builder()
                .perfilAyudante(perfil)
                .urlArchivo(url)
                .tipo(tipo != null ? tipo : TipoEvidencia.FOTO)
                .descripcion(descripcion)
                .build());
        return Mapper.toEvidencia(evidencia);
    }

    @Transactional
    public void eliminar(Long id) {
        PerfilAyudante perfil = perfilAyudanteActual();
        EvidenciaTrabajo evidencia = evidenciaRepository.findByIdAndPerfilAyudanteId(id, perfil.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Evidencia no encontrada"));
        fileStorageService.deleteIfExists(evidencia.getUrlArchivo());
        evidenciaRepository.delete(evidencia);
    }

    private PerfilAyudante perfilAyudanteActual() {
        UserPrincipal principal = SecurityUtils.currentUser();
        return perfilAyudanteRepository.findByUsuarioId(principal.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "Solo ayudantes pueden gestionar evidencias"));
    }
}
