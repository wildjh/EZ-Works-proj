package com.ezworks.dto.user;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PerfilEmpleadorDto {

    private Long id;
    private BigDecimal calificacionPromedio;
    private Integer totalResenas;
}
