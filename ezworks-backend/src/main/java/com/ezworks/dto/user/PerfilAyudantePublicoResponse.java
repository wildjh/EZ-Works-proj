package com.ezworks.dto.user;

import com.ezworks.domain.enums.TipoEvidencia;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class PerfilAyudantePublicoResponse {

    private Long id;
    private Long usuarioId;
    private String nombre;
    private String apellido;
    private String bio;
    private String fotoPerfilUrl;
    private BigDecimal calificacionPromedio;
    private Integer totalResenas;
    private List<EvidenciaPublica> evidencias;

    @Data
    @Builder
    public static class EvidenciaPublica {
        private Long id;
        private String urlArchivo;
        private TipoEvidencia tipo;
        private String descripcion;
        private Instant subidoEn;
    }
}
