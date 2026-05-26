package com.ezworks.controller;

import com.ezworks.dto.user.UpdatePerfilRequest;
import com.ezworks.dto.user.UsuarioResponse;
import com.ezworks.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    public UsuarioResponse me() {
        return usuarioService.getMe();
    }

    @PatchMapping("/me")
    public UsuarioResponse updateMe(@Valid @RequestBody UpdatePerfilRequest request) {
        return usuarioService.updateMe(request);
    }
}
