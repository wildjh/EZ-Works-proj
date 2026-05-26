package com.ezworks.controller;

import com.ezworks.dto.job.*;
import com.ezworks.service.RequerimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requerimientos")
@RequiredArgsConstructor
public class RequerimientoController {

    private final RequerimientoService requerimientoService;

    @GetMapping("/vacantes")
    public List<RequerimientoResponse> vacantes() {
        return requerimientoService.vacantesPublicadas();
    }

    @GetMapping("/mis")
    @PreAuthorize("hasRole('EMPLEADOR')")
    public List<RequerimientoResponse> misRequerimientos() {
        return requerimientoService.misRequerimientos();
    }

    @GetMapping("/{id}")
    public RequerimientoResponse obtener(@PathVariable Long id) {
        return requerimientoService.obtener(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLEADOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public RequerimientoResponse crear(@Valid @RequestBody RequerimientoRequest request) {
        return requerimientoService.crear(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLEADOR')")
    public RequerimientoResponse actualizar(@PathVariable Long id, @Valid @RequestBody RequerimientoRequest request) {
        return requerimientoService.actualizar(id, request);
    }

    @PostMapping("/{id}/publicar")
    @PreAuthorize("hasRole('EMPLEADOR')")
    public RequerimientoResponse publicar(@PathVariable Long id) {
        return requerimientoService.publicar(id);
    }

    @PostMapping("/{id}/postulaciones")
    @PreAuthorize("hasRole('AYUDANTE')")
    @ResponseStatus(HttpStatus.CREATED)
    public PostulacionResponse postular(@PathVariable Long id, @Valid @RequestBody PostulacionRequest request) {
        return requerimientoService.postular(id, request);
    }

    @GetMapping("/{id}/postulaciones")
    @PreAuthorize("hasRole('EMPLEADOR')")
    public List<PostulacionResponse> postulaciones(@PathVariable Long id) {
        return requerimientoService.listarPostulaciones(id);
    }

    @PostMapping("/{id}/match")
    @PreAuthorize("hasRole('EMPLEADOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public EmparejamientoResponse match(@PathVariable Long id, @Valid @RequestBody MatchRequest request) {
        return requerimientoService.crearMatch(id, request);
    }
}
