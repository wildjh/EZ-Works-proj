package com.ezworks.repository;

import com.ezworks.domain.job.Emparejamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmparejamientoRepository extends JpaRepository<Emparejamiento, Long> {

    boolean existsByRequerimientoId(Long requerimientoId);

    Optional<Emparejamiento> findByRequerimientoId(Long requerimientoId);

    @Query("SELECT e FROM Emparejamiento e JOIN FETCH e.requerimiento JOIN FETCH e.postulacion WHERE e.id = :id")
    Optional<Emparejamiento> findByIdWithDetails(@Param("id") Long id);
}
