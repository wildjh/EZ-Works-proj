package com.ezworks.dto.job;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostulacionRequest {

    @Size(max = 500)
    private String mensajePresentacion;
}
