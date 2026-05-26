package com.ezworks.domain.payment;

import com.ezworks.domain.enums.TipoMetodoPago;
import com.ezworks.domain.user.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "metodo_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMetodoPago tipo;

    @Column(nullable = false, length = 80)
    private String alias;

    @Column(name = "ultimos_cuatro", length = 4)
    private String ultimosCuatro;

    @Column(nullable = false)
    @Builder.Default
    private boolean predeterminado = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @PrePersist
    void prePersist() {
        if (creadoEn == null) {
            creadoEn = Instant.now();
        }
    }
}
