package com.ezworks.repository;

import com.ezworks.domain.enums.RolCodigo;
import com.ezworks.domain.user.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Byte> {

    Optional<Rol> findByCodigo(RolCodigo codigo);
}
