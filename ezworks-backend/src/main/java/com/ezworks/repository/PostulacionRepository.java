package com.ezworks.repository;

import com.ezworks.domain.job.Postulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostulacionRepository extends JpaRepository<Postulacion, Long> {

    boolean existsByRequerimientoIdAndAyudanteId(Long requerimientoId, Long ayudanteId);

    List<Postulacion> findByRequerimientoIdOrderByCreadoEnDesc(Long requerimientoId);

    @Query("SELECT p FROM Postulacion p JOIN FETCH p.ayudante a JOIN FETCH a.usuario WHERE p.requerimiento.id = :reqId")
    List<Postulacion> findByRequerimientoIdWithAyudante(@Param("reqId") Long requerimientoId);

    Optional<Postulacion> findByIdAndRequerimientoId(Long id, Long requerimientoId);

    @Query("SELECT p FROM Postulacion p JOIN FETCH p.ayudante a JOIN FETCH a.usuario WHERE p.id = :id")
    Optional<Postulacion> findByIdWithAyudante(@Param("id") Long id);
}
