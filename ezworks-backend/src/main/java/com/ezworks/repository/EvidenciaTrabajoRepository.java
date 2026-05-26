package com.ezworks.repository;

import com.ezworks.domain.user.EvidenciaTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvidenciaTrabajoRepository extends JpaRepository<EvidenciaTrabajo, Long> {

    List<EvidenciaTrabajo> findByPerfilAyudanteIdOrderBySubidoEnDesc(Long perfilAyudanteId);

    Optional<EvidenciaTrabajo> findByIdAndPerfilAyudanteId(Long id, Long perfilAyudanteId);
}
