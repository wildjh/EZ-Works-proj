package com.ezworks.repository;

import com.ezworks.domain.payment.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    List<Transaccion> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);
}
