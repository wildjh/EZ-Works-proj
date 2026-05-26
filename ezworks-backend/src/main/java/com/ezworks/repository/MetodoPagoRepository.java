package com.ezworks.repository;

import com.ezworks.domain.payment.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {

    List<MetodoPago> findByUsuarioIdAndActivoTrueOrderByPredeterminadoDescCreadoEnDesc(Long usuarioId);

    Optional<MetodoPago> findByIdAndUsuarioId(Long id, Long usuarioId);
}
