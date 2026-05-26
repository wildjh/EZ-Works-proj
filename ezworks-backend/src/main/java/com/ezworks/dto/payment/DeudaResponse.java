package com.ezworks.dto.payment;

import com.ezworks.domain.enums.EstadoDeuda;
import com.ezworks.domain.enums.TipoMetodoPago;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class DeudaResponse {

    private Long id;
    private BigDecimal monto;
    private EstadoDeuda estado;
    private String descripcion;
    private String requerimientoTitulo;
    private Instant creadoEn;
    private Instant pagadaEn;
}
