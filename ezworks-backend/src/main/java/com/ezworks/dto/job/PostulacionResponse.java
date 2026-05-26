package com.ezworks.dto.job;

import com.ezworks.domain.enums.EstadoPostulacion;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PostulacionResponse {

    private Long id;
    private Long requerimientoId;
    private Long ayudanteId;
    private String ayudanteNombre;
    private String ayudanteApellido;
    private String mensajePresentacion;
    private EstadoPostulacion estado;
    private Instant creadoEn;
}
