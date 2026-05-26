package com.ezworks.dto.job;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class EmparejamientoResponse {

    private Long id;
    private Long requerimientoId;
    private Long postulacionId;
    private Long conversacionId;
    private Instant establecidoEn;
}
