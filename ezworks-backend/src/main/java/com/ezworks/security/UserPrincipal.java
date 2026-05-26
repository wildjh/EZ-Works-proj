package com.ezworks.security;

import com.ezworks.domain.enums.EstadoCuenta;
import com.ezworks.domain.enums.RolCodigo;
import com.ezworks.domain.user.Usuario;
import com.ezworks.domain.user.UsuarioRol;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final EstadoCuenta estadoCuenta;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.password = usuario.getPasswordHash();
        this.estadoCuenta = usuario.getEstadoCuenta();
        this.authorities = usuario.getRoles().stream()
                .map(UsuarioRol::getRol)
                .map(r -> new SimpleGrantedAuthority(r.getCodigo().asAuthority()))
                .collect(Collectors.toSet());
    }

    public boolean hasRole(RolCodigo rol) {
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals(rol.asAuthority()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return estadoCuenta == EstadoCuenta.ACTIVO;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return estadoCuenta == EstadoCuenta.ACTIVO;
    }
}
