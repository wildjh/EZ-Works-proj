package com.ezworks.dto.messaging;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MensajeRequest {

    @NotBlank
    private String contenido;
}
