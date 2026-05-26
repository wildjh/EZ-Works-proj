package com.ezworks.dto.payment;

import com.ezworks.domain.enums.EstadoTransaccion;
import com.ezworks.domain.enums.TipoTransaccion;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class TransaccionResponse {

    private Long id;
    private TipoTransaccion tipo;
    private BigDecimal monto;
    private EstadoTransaccion estado;
    private String referencia;
    private Instant creadoEn;
    private Instant completadaEn;
}
