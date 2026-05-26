package com.ezworks.repository;

import com.ezworks.domain.enums.EstadoRequerimiento;
import com.ezworks.domain.job.Requerimiento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RequerimientoRepository extends JpaRepository<Requerimiento, Long> {

    @EntityGraph(attributePaths = {"categoria", "empleador"})
    List<Requerimiento> findByEmpleadorIdOrderByActualizadoEnDesc(Long empleadorId);

    @EntityGraph(attributePaths = {"categoria", "empleador"})
    List<Requerimiento> findByEstadoOrderByPublicadoEnDesc(EstadoRequerimiento estado);

    @Query("SELECT r FROM Requerimiento r JOIN FETCH r.categoria JOIN FETCH r.empleador e JOIN FETCH e.usuario WHERE r.id = :id")
    Optional<Requerimiento> findByIdWithDetails(@Param("id") Long id);
}
