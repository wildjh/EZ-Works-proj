package com.ezworks.repository;

import com.ezworks.domain.enums.RolCodigo;
import com.ezworks.domain.user.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles ur LEFT JOIN FETCH ur.rol WHERE u.email = :email")
    Optional<Usuario> findByEmailWithRoles(@Param("email") String email);

    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles ur LEFT JOIN FETCH ur.rol WHERE u.id = :id")
    Optional<Usuario> findByIdWithRoles(@Param("id") Long id);

    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles ur LEFT JOIN FETCH ur.rol")
    List<Usuario> findAllWithRoles();

    @Query("""
            SELECT COUNT(u) > 0
            FROM Usuario u
            JOIN u.roles ur
            JOIN ur.rol r
            WHERE r.codigo = :codigo
            """)
    boolean existsByRolCodigo(@Param("codigo") RolCodigo codigo);
}
