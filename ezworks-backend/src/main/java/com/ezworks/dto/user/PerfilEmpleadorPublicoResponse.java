package com.ezworks.dto.user;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PerfilEmpleadorPublicoResponse {

    private Long id;
    private Long usuarioId;
    private String nombre;
    private String apellido;
    private String fotoPerfilUrl;
    private BigDecimal calificacionPromedio;
    private Integer totalResenas;
}
