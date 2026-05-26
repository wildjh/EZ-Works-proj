package com.ezworks.util;

import com.ezworks.domain.job.*;
import com.ezworks.domain.messaging.Conversacion;
import com.ezworks.domain.messaging.Mensaje;
import com.ezworks.domain.user.*;
import com.ezworks.dto.job.*;
import com.ezworks.dto.messaging.ConversacionResponse;
import com.ezworks.dto.messaging.MensajeResponse;
import com.ezworks.dto.user.*;

import java.util.List;
import java.util.stream.Collectors;

public final class Mapper {

    private Mapper() {}

    public static UsuarioResponse toUsuarioResponse(
            Usuario u,
            PerfilEmpleador pe,
            PerfilAyudante pa) {
        return UsuarioResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .telefono(u.getTelefono())
                .estadoCuenta(u.getEstadoCuenta())
                .roles(u.getRoles().stream()
                        .map(ur -> ur.getRol().getCodigo())
                        .collect(Collectors.toList()))
                .perfilEmpleador(pe != null ? PerfilEmpleadorDto.builder()
                        .id(pe.getId())
                        .calificacionPromedio(pe.getCalificacionPromedio())
                        .totalResenas(pe.getTotalResenas())
                        .build() : null)
                .perfilAyudante(pa != null ? PerfilAyudanteDto.builder()
                        .id(pa.getId())
                        .bio(pa.getBio())
                        .calificacionPromedio(pa.getCalificacionPromedio())
                        .totalResenas(pa.getTotalResenas())
                        .build() : null)
                .creadoEn(u.getCreadoEn())
                .build();
    }

    public static CategoriaResponse toCategoria(Categoria c) {
        return CategoriaResponse.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .activa(c.getActiva())
                .build();
    }

    public static RequerimientoResponse toRequerimiento(Requerimiento r, Long emparejamientoId) {
        return RequerimientoResponse.builder()
                .id(r.getId())
                .titulo(r.getTitulo())
                .descripcion(r.getDescripcion())
                .remuneracion(r.getRemuneracion())
                .estado(r.getEstado())
                .categoriaId(r.getCategoria().getId())
                .categoriaNombre(r.getCategoria().getNombre())
                .empleadorId(r.getEmpleador().getId())
                .zonaAproximada(r.getZonaAproximada())
                .latitudAprox(r.getLatitudAprox())
                .longitudAprox(r.getLongitudAprox())
                .direccionExacta(maskDireccion(r))
                .publicadoEn(r.getPublicadoEn())
                .actualizadoEn(r.getActualizadoEn())
                .emparejamientoId(emparejamientoId)
                .build();
    }

    private static String maskDireccion(Requerimiento r) {
        if (r.getEstado() == com.ezworks.domain.enums.EstadoRequerimiento.EN_MATCH
                || r.getEstado() == com.ezworks.domain.enums.EstadoRequerimiento.FINALIZADO) {
            return r.getDireccionExacta();
        }
        return null;
    }

    public static PostulacionResponse toPostulacion(Postulacion p) {
        var u = p.getAyudante().getUsuario();
        return PostulacionResponse.builder()
                .id(p.getId())
                .requerimientoId(p.getRequerimiento().getId())
                .ayudanteId(p.getAyudante().getId())
                .ayudanteNombre(u.getNombre())
                .ayudanteApellido(u.getApellido())
                .mensajePresentacion(p.getMensajePresentacion())
                .estado(p.getEstado())
                .creadoEn(p.getCreadoEn())
                .build();
    }

    public static MensajeResponse toMensaje(Mensaje m) {
        return MensajeResponse.builder()
                .id(m.getId())
                .conversacionId(m.getConversacion().getId())
                .emisorUsuarioId(m.getEmisor().getId())
                .emisorNombre(m.getEmisor().getNombre())
                .contenido(m.getContenido())
                .leido(m.getLeido())
                .enviadoEn(m.getEnviadoEn())
                .build();
    }

    public static ConversacionResponse toConversacion(Conversacion c, List<Mensaje> mensajes) {
        return ConversacionResponse.builder()
                .id(c.getId())
                .emparejamientoId(c.getEmparejamiento().getId())
                .abiertaEn(c.getAbiertaEn())
                .mensajes(mensajes.stream().map(Mapper::toMensaje).toList())
                .build();
    }

    public static EvidenciaResponse toEvidencia(EvidenciaTrabajo e) {
        return EvidenciaResponse.builder()
                .id(e.getId())
                .urlArchivo(e.getUrlArchivo())
                .tipo(e.getTipo())
                .descripcion(e.getDescripcion())
                .subidoEn(e.getSubidoEn())
                .build();
    }
}
