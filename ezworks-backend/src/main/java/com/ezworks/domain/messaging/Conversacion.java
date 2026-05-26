package com.ezworks.domain.messaging;

import com.ezworks.domain.job.Emparejamiento;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "conversacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emparejamiento_id", nullable = false, unique = true)
    private Emparejamiento emparejamiento;

    @Column(name = "abierta_en", nullable = false, updatable = false)
    private Instant abiertaEn;

    @PrePersist
    void prePersist() {
        if (abiertaEn == null) {
            abiertaEn = Instant.now();
        }
    }
}
