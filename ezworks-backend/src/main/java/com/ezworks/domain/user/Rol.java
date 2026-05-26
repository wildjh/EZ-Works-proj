package com.ezworks.domain.user;

import com.ezworks.domain.enums.RolCodigo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "rol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(columnDefinition = "TINYINT UNSIGNED")
    private Byte id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 30)
    private RolCodigo codigo;

    @Column(nullable = false, length = 80)
    private String nombre;
}
