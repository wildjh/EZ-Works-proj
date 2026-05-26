package com.ezworks.dto.admin;

import com.ezworks.domain.enums.EstadoCuenta;
import com.ezworks.domain.enums.RolCodigo;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class AdminUsuarioResponse {

    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String telefono;
    private String fotoPerfilUrl;
    private EstadoCuenta estadoCuenta;
    private List<RolCodigo> roles;
    private Instant creadoEn;
}
