package com.ezworks.dto.user;

import com.ezworks.domain.enums.TipoEvidencia;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class EvidenciaResponse {

    private Long id;
    private String urlArchivo;
    private TipoEvidencia tipo;
    private String descripcion;
    private Instant subidoEn;
}
