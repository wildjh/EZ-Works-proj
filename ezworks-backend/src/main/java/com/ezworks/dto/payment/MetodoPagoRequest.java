package com.ezworks.dto.payment;

import com.ezworks.domain.enums.TipoMetodoPago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MetodoPagoRequest {

    @NotNull
    private TipoMetodoPago tipo;

    @NotBlank
    @Size(max = 80)
    private String alias;

    @Size(max = 4)
    private String ultimosCuatro;

    private boolean predeterminado;
}
