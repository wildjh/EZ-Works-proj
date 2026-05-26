package com.ezworks.dto.job;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoriaResponse {

    private Short id;
    private String nombre;
    private Boolean activa;
}
