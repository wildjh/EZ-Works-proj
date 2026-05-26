package com.ezworks.dto.auth;

import com.ezworks.domain.enums.RolCodigo;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotBlank
    @Size(max = 100)
    private String apellido;

    @Size(max = 20)
    private String telefono;

    @NotEmpty
    private Set<RolCodigo> roles;

    @AssertTrue(message = "Debe aceptar los términos y condiciones")
    private boolean aceptaTerminos;
}
