package com.ezworks.dto.payment;

import com.ezworks.domain.enums.TipoMetodoPago;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class MetodoPagoResponse {

    private Long id;
    private TipoMetodoPago tipo;
    private String alias;
    private String ultimosCuatro;
    private boolean predeterminado;
    private Instant creadoEn;
}
