package com.ezworks.repository;

import com.ezworks.domain.messaging.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    @Query("""
            SELECT m
            FROM Mensaje m
            JOIN FETCH m.emisor
            WHERE m.conversacion.id = :conversacionId
            ORDER BY m.enviadoEn ASC
            """)
    List<Mensaje> findByConversacionIdWithEmisorOrderByEnviadoEnAsc(@Param("conversacionId") Long conversacionId);

    Optional<Mensaje> findFirstByConversacionIdOrderByEnviadoEnAsc(Long conversacionId);
}
