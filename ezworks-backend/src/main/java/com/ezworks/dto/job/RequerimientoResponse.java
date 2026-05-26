package com.ezworks.dto.job;

import com.ezworks.domain.enums.EstadoRequerimiento;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class RequerimientoResponse {

    private Long id;
    private String titulo;
    private String descripcion;
    private BigDecimal remuneracion;
    private EstadoRequerimiento estado;
    private Short categoriaId;
    private String categoriaNombre;
    private Long empleadorId;
    private String zonaAproximada;
    private BigDecimal latitudAprox;
    private BigDecimal longitudAprox;
    private String direccionExacta;
    private Instant publicadoEn;
    private Instant actualizadoEn;
    private Long emparejamientoId;
}
