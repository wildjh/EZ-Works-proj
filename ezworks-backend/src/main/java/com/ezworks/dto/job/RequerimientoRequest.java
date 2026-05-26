package com.ezworks.dto.job;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequerimientoRequest {

    @NotNull
    private Short categoriaId;

    @NotBlank
    @Size(max = 150)
    private String titulo;

    @NotBlank
    private String descripcion;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal remuneracion;

    @Size(max = 200)
    private String zonaAproximada;

    private BigDecimal latitudAprox;
    private BigDecimal longitudAprox;

    @Size(max = 300)
    private String direccionExacta;

    private BigDecimal latitudExacta;
    private BigDecimal longitudExacta;
}
