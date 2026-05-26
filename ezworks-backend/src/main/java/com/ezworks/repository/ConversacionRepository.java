package com.ezworks.repository;

import com.ezworks.domain.messaging.Conversacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversacionRepository extends JpaRepository<Conversacion, Long> {

    Optional<Conversacion> findByEmparejamientoId(Long emparejamientoId);

    @Query("""
            SELECT c
            FROM Conversacion c
            JOIN FETCH c.emparejamiento e
            JOIN FETCH e.requerimiento
            JOIN FETCH e.empleador pe
            JOIN FETCH pe.usuario
            JOIN FETCH e.ayudante pa
            JOIN FETCH pa.usuario
            WHERE c.id = :id
            """)
    Optional<Conversacion> findByIdWithDetails(@Param("id") Long id);

    @Query("""
            SELECT c
            FROM Conversacion c
            JOIN FETCH c.emparejamiento e
            JOIN FETCH e.requerimiento
            JOIN FETCH e.empleador pe
            JOIN FETCH pe.usuario
            JOIN FETCH e.ayudante pa
            JOIN FETCH pa.usuario
            WHERE e.id = :emparejamientoId
            """)
    Optional<Conversacion> findByEmparejamientoIdWithDetails(@Param("emparejamientoId") Long emparejamientoId);

    @Query("""
            SELECT DISTINCT c
            FROM Conversacion c
            JOIN FETCH c.emparejamiento e
            JOIN FETCH e.requerimiento
            JOIN FETCH e.empleador pe
            JOIN FETCH pe.usuario
            JOIN FETCH e.ayudante pa
            JOIN FETCH pa.usuario
            WHERE pe.usuario.id = :usuarioId OR pa.usuario.id = :usuarioId
            ORDER BY c.abiertaEn DESC
            """)
    List<Conversacion> findAllForUsuarioWithDetails(@Param("usuarioId") Long usuarioId);
}
