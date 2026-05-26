package com.ezworks.repository;

import com.ezworks.domain.user.PerfilAyudante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PerfilAyudanteRepository extends JpaRepository<PerfilAyudante, Long> {

    Optional<PerfilAyudante> findByUsuarioId(Long usuarioId);

    @Query("SELECT pa FROM PerfilAyudante pa JOIN FETCH pa.usuario WHERE pa.id = :id")
    Optional<PerfilAyudante> findByIdWithUsuario(@Param("id") Long id);
}
