package com.ezworks.dto.messaging;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class MensajeResponse {

    private Long id;
    private Long conversacionId;
    private Long emisorUsuarioId;
    private String emisorNombre;
    private String contenido;
    private Boolean leido;
    private Instant enviadoEn;
}
