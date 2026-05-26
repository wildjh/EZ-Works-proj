package com.ezworks.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UsuarioRolId implements Serializable {

    private Long usuarioId;

    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "rol_id", columnDefinition = "TINYINT UNSIGNED")
    private Byte rolId;
}
