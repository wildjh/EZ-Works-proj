package com.ezworks.controller;

import com.ezworks.dto.user.PerfilAyudantePublicoResponse;
import com.ezworks.dto.user.PerfilEmpleadorPublicoResponse;
import com.ezworks.service.PerfilPublicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/perfiles")
@RequiredArgsConstructor
public class PerfilPublicoController {

    private final PerfilPublicoService perfilPublicoService;

    @GetMapping("/ayudante/{perfilAyudanteId}")
    public PerfilAyudantePublicoResponse obtenerAyudante(@PathVariable Long perfilAyudanteId) {
        return perfilPublicoService.obtenerPerfilAyudante(perfilAyudanteId);
    }

    @GetMapping("/empleador/{perfilEmpleadorId}")
    public PerfilEmpleadorPublicoResponse obtenerEmpleador(@PathVariable Long perfilEmpleadorId) {
        return perfilPublicoService.obtenerPerfilEmpleador(perfilEmpleadorId);
    }
}
