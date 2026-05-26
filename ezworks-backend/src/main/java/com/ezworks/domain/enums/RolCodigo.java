package com.ezworks.domain.enums;

public enum RolCodigo {
    EMPLEADOR,
    AYUDANTE,
    ADMIN;

    public String asAuthority() {
        return "ROLE_" + name();
    }
}
