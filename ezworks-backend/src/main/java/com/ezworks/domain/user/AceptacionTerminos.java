package com.ezworks.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "aceptacion_terminos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AceptacionTerminos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "version_terminos", nullable = false, length = 20)
    private String versionTerminos;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "aceptado_en", nullable = false, updatable = false)
    private Instant aceptadoEn;

    @PrePersist
    void prePersist() {
        if (aceptadoEn == null) {
            aceptadoEn = Instant.now();
        }
    }
}
