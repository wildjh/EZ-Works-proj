package com.ezworks.dto.user;

import com.ezworks.domain.enums.EstadoCuenta;
import com.ezworks.domain.enums.RolCodigo;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class UsuarioResponse {

    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String telefono;
    private EstadoCuenta estadoCuenta;
    private List<RolCodigo> roles;
    private PerfilEmpleadorDto perfilEmpleador;
    private PerfilAyudanteDto perfilAyudante;
    private Instant creadoEn;
}
