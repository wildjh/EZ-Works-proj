package com.ezworks.domain.job;

import com.ezworks.domain.user.PerfilAyudante;
import com.ezworks.domain.user.PerfilEmpleador;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "emparejamiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Emparejamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requerimiento_id", nullable = false, unique = true)
    private Requerimiento requerimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleador_id", nullable = false)
    private PerfilEmpleador empleador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ayudante_id", nullable = false)
    private PerfilAyudante ayudante;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postulacion_id", nullable = false, unique = true)
    private Postulacion postulacion;

    @Column(name = "establecido_en", nullable = false, updatable = false)
    private Instant establecidoEn;

    @Column(name = "finalizado_en")
    private Instant finalizadoEn;

    @PrePersist
    void prePersist() {
        if (establecidoEn == null) {
            establecidoEn = Instant.now();
        }
    }
}
