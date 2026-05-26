package com.ezworks.dto.admin;

import com.ezworks.domain.enums.EstadoCuenta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarEstadoCuentaRequest {

    @NotNull
    private EstadoCuenta estadoCuenta;

    @Size(max = 500)
    private String motivo;
}
