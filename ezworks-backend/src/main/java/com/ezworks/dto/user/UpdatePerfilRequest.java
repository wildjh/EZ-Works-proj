package com.ezworks.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePerfilRequest {

    @Size(max = 100)
    private String nombre;

    @Size(max = 100)
    private String apellido;

    @Size(max = 20)
    private String telefono;

    @Size(max = 2000)
    private String bio;
}
