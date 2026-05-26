package com.ezworks.controller;

import com.ezworks.dto.admin.ActualizarEstadoCuentaRequest;
import com.ezworks.dto.admin.AdminUsuarioResponse;
import com.ezworks.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public List<AdminUsuarioResponse> listar() {
        return adminService.listarUsuarios();
    }

    @PatchMapping("/{id}/estado")
    public AdminUsuarioResponse actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoCuentaRequest request) {
        return adminService.actualizarEstado(id, request);
    }
}
