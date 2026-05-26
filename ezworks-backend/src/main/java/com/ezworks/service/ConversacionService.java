package com.ezworks.service;

import com.ezworks.domain.enums.EstadoRequerimiento;
import com.ezworks.domain.job.Emparejamiento;
import com.ezworks.domain.messaging.Conversacion;
import com.ezworks.domain.messaging.Mensaje;
import com.ezworks.domain.user.Usuario;
import com.ezworks.dto.messaging.ConversacionResponse;
import com.ezworks.dto.messaging.ConversacionResumenResponse;
import com.ezworks.dto.messaging.MensajeRequest;
import com.ezworks.dto.messaging.MensajeResponse;
import com.ezworks.exception.ApiException;
import com.ezworks.repository.ConversacionRepository;
import com.ezworks.repository.MensajeRepository;
import com.ezworks.util.Mapper;
import com.ezworks.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversacionService {

    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    public List<ConversacionResumenResponse> listarMisConversaciones() {
        Long usuarioId = SecurityUtils.currentUser().getId();
        return conversacionRepository.findAllForUsuarioWithDetails(usuarioId).stream()
                .map(c -> toResumenResponse(c, usuarioId))
                .toList();
    }

    @Transactional(readOnly = true)
    public ConversacionResponse obtener(Long conversacionId) {
        Long usuarioId = SecurityUtils.currentUser().getId();
        Conversacion conversacion = loadConversacionWithAccess(conversacionId, usuarioId);
        return toConversacionResponse(conversacion, usuarioId);
    }

    @Transactional(readOnly = true)
    public ConversacionResponse obtenerPorEmparejamiento(Long emparejamientoId) {
        Long usuarioId = SecurityUtils.currentUser().getId();
        Conversacion conversacion = conversacionRepository.findByEmparejamientoIdWithDetails(emparejamientoId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Conversación no encontrada"));
        ensureParticipante(conversacion, usuarioId);
        return toConversacionResponse(conversacion, usuarioId);
    }

    @Transactional
    public MensajeResponse enviarMensaje(Long conversacionId, MensajeRequest request) {
        Long usuarioId = SecurityUtils.currentUser().getId();
        Conversacion conversacion = loadConversacionWithAccess(conversacionId, usuarioId);

        Emparejamiento emparejamiento = conversacion.getEmparejamiento();
        Usuario empleadorUsuario = emparejamiento.getEmpleador().getUsuario();
        Usuario ayudanteUsuario = emparejamiento.getAyudante().getUsuario();
        boolean esEmpleador = empleadorUsuario.getId().equals(usuarioId);
        boolean esAyudante = ayudanteUsuario.getId().equals(usuarioId);

        if (!esEmpleador && !esAyudante) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No autorizado para enviar mensajes en esta conversación");
        }
        if (emparejamiento.getRequerimiento().getEstado() != EstadoRequerimiento.EN_MATCH) {
            throw new ApiException(HttpStatus.CONFLICT, "La conversación está cerrada");
        }

        boolean empleadorEnvioPrimerMensaje = mensajeRepository.findFirstByConversacionIdOrderByEnviadoEnAsc(conversacionId)
                .map(m -> m.getEmisor().getId().equals(empleadorUsuario.getId()))
                .orElse(false);
        if (esAyudante && !empleadorEnvioPrimerMensaje) {
            throw new ApiException(HttpStatus.CONFLICT, "El empleador debe iniciar la conversación");
        }

        Usuario emisor = esEmpleador ? empleadorUsuario : ayudanteUsuario;
        Mensaje saved = mensajeRepository.save(Mensaje.builder()
                .conversacion(conversacion)
                .emisor(emisor)
                .contenido(request.getContenido().trim())
                .build());

        MensajeResponse response = Mapper.toMensaje(saved);
        messagingTemplate.convertAndSend("/topic/conversaciones/" + conversacionId, response);
        return response;
    }

    private Conversacion loadConversacionWithAccess(Long conversacionId, Long usuarioId) {
        Conversacion conversacion = conversacionRepository.findByIdWithDetails(conversacionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Conversación no encontrada"));
        ensureParticipante(conversacion, usuarioId);
        return conversacion;
    }

    private void ensureParticipante(Conversacion conversacion, Long usuarioId) {
        Emparejamiento e = conversacion.getEmparejamiento();
        boolean esEmpleador = e.getEmpleador().getUsuario().getId().equals(usuarioId);
        boolean esAyudante = e.getAyudante().getUsuario().getId().equals(usuarioId);
        if (!esEmpleador && !esAyudante) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No autorizado para acceder a esta conversación");
        }
    }

    private ConversacionResponse toConversacionResponse(Conversacion conversacion, Long usuarioId) {
        Emparejamiento e = conversacion.getEmparejamiento();
        Usuario empleadorUsuario = e.getEmpleador().getUsuario();
        Usuario ayudanteUsuario = e.getAyudante().getUsuario();
        boolean esEmpleador = empleadorUsuario.getId().equals(usuarioId);
        Usuario otro = esEmpleador ? ayudanteUsuario : empleadorUsuario;
        Long otroPerfilAyudanteId = esEmpleador ? e.getAyudante().getId() : null;
        Long otroPerfilEmpleadorId = esEmpleador ? null : e.getEmpleador().getId();
        EstadoRequerimiento estado = e.getRequerimiento().getEstado();
        boolean activa = estado == EstadoRequerimiento.EN_MATCH;

        List<MensajeResponse> mensajes = mensajeRepository
                .findByConversacionIdWithEmisorOrderByEnviadoEnAsc(conversacion.getId()).stream()
                .map(Mapper::toMensaje)
                .toList();
        boolean empleadorEnvioPrimerMensaje = !mensajes.isEmpty()
                && mensajes.get(0).getEmisorUsuarioId().equals(empleadorUsuario.getId());
        boolean puedeEnviar = activa && (esEmpleador || empleadorEnvioPrimerMensaje);

        return ConversacionResponse.builder()
                .id(conversacion.getId())
                .emparejamientoId(e.getId())
                .requerimientoId(e.getRequerimiento().getId())
                .requerimientoEstado(estado)
                .activa(activa)
                .abiertaEn(conversacion.getAbiertaEn())
                .requerimientoTitulo(e.getRequerimiento().getTitulo())
                .otroParticipanteNombre(otro.getNombre())
                .otroParticipanteApellido(otro.getApellido())
                .otroParticipanteFotoUrl(otro.getFotoPerfilUrl())
                .otroParticipantePerfilAyudanteId(otroPerfilAyudanteId)
                .otroParticipantePerfilEmpleadorId(otroPerfilEmpleadorId)
                .empleadorUsuarioId(empleadorUsuario.getId())
                .empleadorEnvioPrimerMensaje(empleadorEnvioPrimerMensaje)
                .puedeEnviar(puedeEnviar)
                .mensajes(mensajes)
                .build();
    }

    private ConversacionResumenResponse toResumenResponse(Conversacion conversacion, Long usuarioId) {
        Emparejamiento e = conversacion.getEmparejamiento();
        boolean esEmpleador = e.getEmpleador().getUsuario().getId().equals(usuarioId);
        Usuario otro = esEmpleador ? e.getAyudante().getUsuario() : e.getEmpleador().getUsuario();
        String ultimoMensaje = mensajeRepository.findByConversacionIdWithEmisorOrderByEnviadoEnAsc(conversacion.getId())
                .stream()
                .reduce((first, second) -> second)
                .map(Mensaje::getContenido)
                .orElse(null);

        return ConversacionResumenResponse.builder()
                .id(conversacion.getId())
                .requerimientoId(e.getRequerimiento().getId())
                .requerimientoEstado(e.getRequerimiento().getEstado())
                .activa(e.getRequerimiento().getEstado() == EstadoRequerimiento.EN_MATCH)
                .requerimientoTitulo(e.getRequerimiento().getTitulo())
                .otroParticipanteNombre(otro.getNombre())
                .otroParticipanteApellido(otro.getApellido())
                .otroParticipanteFotoUrl(otro.getFotoPerfilUrl())
                .ultimoMensaje(ultimoMensaje)
                .abiertaEn(conversacion.getAbiertaEn())
                .build();
    }
}
