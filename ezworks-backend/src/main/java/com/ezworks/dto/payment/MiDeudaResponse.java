package com.ezworks.dto.payment;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class MiDeudaResponse {

    private BigDecimal saldoDeudaAcumulado;
    private BigDecimal limiteDeudaMaxima;
    private boolean inhabilitadoPorDeuda;
    private List<DeudaResponse> deudasPendientes;
    private List<DeudaResponse> historialDeudas;
    private List<TransaccionResponse> transaccionesRecientes;
}
