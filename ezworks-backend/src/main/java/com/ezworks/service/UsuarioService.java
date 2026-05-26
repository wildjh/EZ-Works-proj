package com.ezworks.service;

import com.ezworks.domain.user.PerfilAyudante;
import com.ezworks.domain.user.PerfilEmpleador;
import com.ezworks.domain.user.Usuario;
import com.ezworks.dto.user.UpdatePerfilRequest;
import com.ezworks.dto.user.UsuarioResponse;
import com.ezworks.exception.ApiException;
import com.ezworks.repository.PerfilAyudanteRepository;
import com.ezworks.repository.PerfilEmpleadorRepository;
import com.ezworks.repository.UsuarioRepository;
import com.ezworks.security.UserPrincipal;
import com.ezworks.util.Mapper;
import com.ezworks.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilEmpleadorRepository perfilEmpleadorRepository;
    private final PerfilAyudanteRepository perfilAyudanteRepository;

    @Transactional(readOnly = true)
    public UsuarioResponse getMe() {
        UserPrincipal principal = SecurityUtils.currentUser();
        Usuario u = usuarioRepository.findByIdWithRoles(principal.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        PerfilEmpleador pe = perfilEmpleadorRepository.findByUsuarioId(u.getId()).orElse(null);
        PerfilAyudante pa = perfilAyudanteRepository.findByUsuarioId(u.getId()).orElse(null);
        return Mapper.toUsuarioResponse(u, pe, pa);
    }

    @Transactional
    public UsuarioResponse updateMe(UpdatePerfilRequest req) {
        UserPrincipal principal = SecurityUtils.currentUser();
        Usuario u = usuarioRepository.findById(principal.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (req.getNombre() != null) u.setNombre(req.getNombre());
        if (req.getApellido() != null) u.setApellido(req.getApellido());
        if (req.getTelefono() != null) u.setTelefono(req.getTelefono());

        perfilAyudanteRepository.findByUsuarioId(u.getId()).ifPresent(pa -> {
            if (req.getBio() != null) pa.setBio(req.getBio());
        });

        usuarioRepository.save(u);
        return getMe();
    }
}
