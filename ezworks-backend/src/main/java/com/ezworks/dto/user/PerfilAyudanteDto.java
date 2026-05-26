package com.ezworks.dto.user;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PerfilAyudanteDto {

    private Long id;
    private String bio;
    private BigDecimal calificacionPromedio;
    private Integer totalResenas;
}
