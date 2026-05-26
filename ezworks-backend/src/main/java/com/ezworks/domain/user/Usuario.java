package com.ezworks.domain.user;

import com.ezworks.domain.enums.EstadoCuenta;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(length = 20)
    private String telefono;

    @Column(name = "foto_perfil_url", length = 512)
    private String fotoPerfilUrl;

    @Column(name = "saldo_deuda_acumulado", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal saldoDeudaAcumulado = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cuenta", nullable = false)
    @Builder.Default
    private EstadoCuenta estadoCuenta = EstadoCuenta.ACTIVO;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UsuarioRol> roles = new HashSet<>();

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        creadoEn = now;
        actualizadoEn = now;
    }

    @PreUpdate
    void preUpdate() {
        actualizadoEn = Instant.now();
    }
}
