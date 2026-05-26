package com.ezworks.repository;

import com.ezworks.domain.user.PerfilEmpleador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PerfilEmpleadorRepository extends JpaRepository<PerfilEmpleador, Long> {

    Optional<PerfilEmpleador> findByUsuarioId(Long usuarioId);

    @Query("SELECT pe FROM PerfilEmpleador pe JOIN FETCH pe.usuario WHERE pe.id = :id")
    Optional<PerfilEmpleador> findByIdWithUsuario(@Param("id") Long id);
}
