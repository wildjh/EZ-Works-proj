package com.ezworks.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "usuario_rol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRol {

    @EmbeddedId
    private UsuarioRolId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rolId")
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @Column(name = "asignado_en", nullable = false, updatable = false)
    private Instant asignadoEn;

    @PrePersist
    void prePersist() {
        if (asignadoEn == null) {
            asignadoEn = Instant.now();
        }
        if (id == null && usuario != null && rol != null) {
            id = new UsuarioRolId(usuario.getId(), rol.getId());
        }
    }
}
