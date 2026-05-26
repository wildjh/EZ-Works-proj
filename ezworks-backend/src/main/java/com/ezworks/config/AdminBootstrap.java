package com.ezworks.config;

import com.ezworks.domain.enums.RolCodigo;
import com.ezworks.domain.user.Rol;
import com.ezworks.domain.user.Usuario;
import com.ezworks.domain.user.UsuarioRol;
import com.ezworks.domain.user.UsuarioRolId;
import com.ezworks.repository.RolRepository;
import com.ezworks.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminBootstrap implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ezworks.admin.bootstrap-enabled:true}")
    private boolean bootstrapEnabled;

    @Value("${ezworks.admin.bootstrap-email:admin@ezworks.local}")
    private String bootstrapEmail;

    @Value("${ezworks.admin.bootstrap-password:AdminEzworks2026!}")
    private String bootstrapPassword;

    @Value("${ezworks.admin.bootstrap-nombre:Administrador}")
    private String bootstrapNombre;

    @Value("${ezworks.admin.bootstrap-apellido:EZWorks}")
    private String bootstrapApellido;

    @Override
    public void run(ApplicationArguments args) {
        if (!bootstrapEnabled || usuarioRepository.existsByRolCodigo(RolCodigo.ADMIN)) {
            return;
        }

        Rol adminRol = rolRepository.findByCodigo(RolCodigo.ADMIN)
                .orElseThrow(() -> new IllegalStateException("Rol ADMIN no encontrado en base de datos"));

        Usuario admin = usuarioRepository.save(Usuario.builder()
                .email(bootstrapEmail.toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(bootstrapPassword))
                .nombre(bootstrapNombre)
                .apellido(bootstrapApellido)
                .build());

        admin.getRoles().add(UsuarioRol.builder()
                .id(new UsuarioRolId(admin.getId(), adminRol.getId()))
                .usuario(admin)
                .rol(adminRol)
                .build());
        usuarioRepository.save(admin);

        log.warn("Cuenta administrador inicial creada: {} — cambie la contraseña en producción", bootstrapEmail);
    }
}
