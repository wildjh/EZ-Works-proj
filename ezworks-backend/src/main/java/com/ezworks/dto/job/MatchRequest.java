package com.ezworks.dto.job;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MatchRequest {

    @NotNull
    private Long postulacionId;
}
