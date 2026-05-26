package com.ezworks.domain.job;

import com.ezworks.domain.enums.EstadoRequerimiento;
import com.ezworks.domain.user.PerfilEmpleador;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "requerimiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Requerimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleador_id", nullable = false)
    private PerfilEmpleador empleador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal remuneracion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoRequerimiento estado = EstadoRequerimiento.BORRADOR;

    @Column(name = "zona_aproximada", length = 200)
    private String zonaAproximada;

    @Column(name = "latitud_aprox", precision = 10, scale = 7)
    private BigDecimal latitudAprox;

    @Column(name = "longitud_aprox", precision = 10, scale = 7)
    private BigDecimal longitudAprox;

    @Column(name = "latitud_exacta", precision = 10, scale = 7)
    private BigDecimal latitudExacta;

    @Column(name = "longitud_exacta", precision = 10, scale = 7)
    private BigDecimal longitudExacta;

    @Column(name = "direccion_exacta", length = 300)
    private String direccionExacta;

    @Column(name = "publicado_en")
    private Instant publicadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    @Column(name = "finalizado_en")
    private Instant finalizadoEn;

    @PrePersist
    void prePersist() {
        if (actualizadoEn == null) {
            actualizadoEn = Instant.now();
        }
    }

    @PreUpdate
    void preUpdate() {
        actualizadoEn = Instant.now();
    }
}
