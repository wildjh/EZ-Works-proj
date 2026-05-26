package com.ezworks.domain.user;

import com.ezworks.domain.enums.TipoEvidencia;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "evidencia_trabajo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvidenciaTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_ayudante_id", nullable = false)
    private PerfilAyudante perfilAyudante;

    @Column(name = "url_archivo", nullable = false, length = 500)
    private String urlArchivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TipoEvidencia tipo = TipoEvidencia.FOTO;

    @Column(length = 200)
    private String descripcion;

    @Column(name = "subido_en", nullable = false, updatable = false)
    private Instant subidoEn;

    @PrePersist
    void prePersist() {
        if (subidoEn == null) {
            subidoEn = Instant.now();
        }
    }
}
