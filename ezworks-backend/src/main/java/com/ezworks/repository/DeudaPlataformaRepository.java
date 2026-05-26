package com.ezworks.repository;

import com.ezworks.domain.enums.EstadoDeuda;
import com.ezworks.domain.payment.DeudaPlataforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeudaPlataformaRepository extends JpaRepository<DeudaPlataforma, Long> {

    List<DeudaPlataforma> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);

    @Query("""
            SELECT d FROM DeudaPlataforma d
            JOIN FETCH d.emparejamiento e
            JOIN FETCH e.requerimiento
            WHERE d.usuario.id = :usuarioId
            ORDER BY d.creadoEn DESC
            """)
    List<DeudaPlataforma> findByUsuarioIdWithDetailsOrderByCreadoEnDesc(@Param("usuarioId") Long usuarioId);

    List<DeudaPlataforma> findByUsuarioIdAndEstadoOrderByCreadoEnDesc(Long usuarioId, EstadoDeuda estado);

    @Query("""
            SELECT d FROM DeudaPlataforma d
            JOIN FETCH d.emparejamiento e
            JOIN FETCH e.requerimiento
            WHERE d.id = :id AND d.usuario.id = :usuarioId
            """)
    Optional<DeudaPlataforma> findByIdAndUsuarioIdWithDetails(
            @Param("id") Long id, @Param("usuarioId") Long usuarioId);

    boolean existsByEmparejamientoId(Long emparejamientoId);
}
