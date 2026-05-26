package com.ezworks.service;

import com.ezworks.domain.enums.EstadoCuenta;
import com.ezworks.domain.enums.RolCodigo;
import com.ezworks.domain.user.*;
import com.ezworks.dto.auth.*;
import com.ezworks.exception.ApiException;
import com.ezworks.repository.*;
import com.ezworks.security.JwtService;
import com.ezworks.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PerfilEmpleadorRepository perfilEmpleadorRepository;
    private final PerfilAyudanteRepository perfilAyudanteRepository;
    private final AceptacionTerminosRepository aceptacionTerminosRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${ezworks.terminos.version}")
    private String terminosVersion;

    @Transactional
    public AuthResponse register(RegisterRequest req, String clientIp) {
        if (usuarioRepository.existsByEmail(req.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        if (!req.isAceptaTerminos()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Debe aceptar términos y condiciones");
        }
        if (req.getRoles().contains(RolCodigo.ADMIN)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No puede auto-registrarse como administrador");
        }

        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .email(req.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .nombre(req.getNombre())
                .apellido(req.getApellido())
                .telefono(req.getTelefono())
                .estadoCuenta(EstadoCuenta.ACTIVO)
                .build());

        for (RolCodigo codigo : req.getRoles()) {
            Rol rol = rolRepository.findByCodigo(codigo)
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Rol inválido: " + codigo));
            UsuarioRol ur = UsuarioRol.builder()
                    .id(new UsuarioRolId(usuario.getId(), rol.getId()))
                    .usuario(usuario)
                    .rol(rol)
                    .build();
            usuario.getRoles().add(ur);
        }
        usuarioRepository.save(usuario);

        if (req.getRoles().contains(RolCodigo.EMPLEADOR)) {
            perfilEmpleadorRepository.save(PerfilEmpleador.builder().usuario(usuario).build());
        }
        if (req.getRoles().contains(RolCodigo.AYUDANTE)) {
            perfilAyudanteRepository.save(PerfilAyudante.builder().usuario(usuario).build());
        }

        aceptacionTerminosRepository.save(AceptacionTerminos.builder()
                .usuario(usuario)
                .versionTerminos(terminosVersion)
                .ipOrigen(clientIp)
                .build());

        return buildAuthResponse(usuarioRepository.findByEmailWithRoles(usuario.getEmail()).orElseThrow());
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail().toLowerCase().trim(), req.getPassword()));
        Usuario usuario = usuarioRepository.findByEmailWithRoles(req.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        return buildAuthResponse(usuario);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest req) {
        String hash = hashToken(req.getRefreshToken());
        RefreshToken stored = refreshTokenRepository.findByTokenHashAndRevocadoFalse(hash)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token inválido"));

        if (stored.getExpiraEn().isBefore(Instant.now())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token expirado");
        }

        stored.setRevocado(true);
        refreshTokenRepository.save(stored);

        Usuario usuario = usuarioRepository.findByIdWithRoles(stored.getUsuario().getId())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        return buildAuthResponse(usuario);
    }

    private AuthResponse buildAuthResponse(Usuario usuario) {
        UserPrincipal principal = new UserPrincipal(usuario);
        String access = jwtService.generateAccessToken(principal);
        String refreshValue = jwtService.generateRefreshTokenValue();
        String refreshHash = hashToken(refreshValue);

        refreshTokenRepository.save(RefreshToken.builder()
                .usuario(usuario)
                .tokenHash(refreshHash)
                .expiraEn(Instant.now().plusMillis(jwtService.getRefreshExpirationMs()))
                .build());

        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refreshValue)
                .userId(usuario.getId())
                .email(usuario.getEmail())
                .roles(usuario.getRoles().stream()
                        .map(ur -> ur.getRol().getCodigo().name())
                        .collect(Collectors.toList()))
                .mensaje("Autenticación exitosa")
                .build();
    }

    private String hashToken(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar token");
        }
    }
}
