package com.ezworks.controller;

import com.ezworks.domain.enums.TipoEvidencia;
import com.ezworks.dto.user.EvidenciaResponse;
import com.ezworks.service.EvidenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/evidencias")
@RequiredArgsConstructor
public class EvidenciaController {

    private final EvidenciaService evidenciaService;

    @GetMapping("/mis")
    public List<EvidenciaResponse> misEvidencias() {
        return evidenciaService.listarMisEvidencias();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EvidenciaResponse subir(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "tipo", required = false) TipoEvidencia tipo) {
        return evidenciaService.subir(archivo, descripcion, tipo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        evidenciaService.eliminar(id);
    }
}
