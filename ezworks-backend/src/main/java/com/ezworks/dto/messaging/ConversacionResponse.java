package com.ezworks.dto.messaging;

import com.ezworks.domain.enums.EstadoRequerimiento;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ConversacionResponse {

    private Long id;
    private Long emparejamientoId;
    private Long requerimientoId;
    private EstadoRequerimiento requerimientoEstado;
    private boolean activa;
    private Instant abiertaEn;
    private String requerimientoTitulo;
    private String otroParticipanteNombre;
    private String otroParticipanteApellido;
    private String otroParticipanteFotoUrl;
    private Long otroParticipantePerfilAyudanteId;
    private Long otroParticipantePerfilEmpleadorId;
    private Long empleadorUsuarioId;
    private boolean empleadorEnvioPrimerMensaje;
    private boolean puedeEnviar;
    private List<MensajeResponse> mensajes;
}
