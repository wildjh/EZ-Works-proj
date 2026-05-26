package com.ezworks.dto.messaging;

import com.ezworks.domain.enums.EstadoRequerimiento;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ConversacionResumenResponse {

    private Long id;
    private Long requerimientoId;
    private EstadoRequerimiento requerimientoEstado;
    private boolean activa;
    private String requerimientoTitulo;
    private String otroParticipanteNombre;
    private String otroParticipanteApellido;
    private String otroParticipanteFotoUrl;
    private String ultimoMensaje;
    private Instant abiertaEn;
}
