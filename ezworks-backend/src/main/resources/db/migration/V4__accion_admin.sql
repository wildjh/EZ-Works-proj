CREATE TABLE accion_admin (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    admin_usuario_id BIGINT UNSIGNED NOT NULL,
    usuario_afectado_id BIGINT UNSIGNED NOT NULL,
    tipo ENUM('ADVERTENCIA', 'SUSPENSION', 'BAN', 'HABILITAR') NOT NULL,
    motivo VARCHAR(500),
    creado_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    FOREIGN KEY (admin_usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (usuario_afectado_id) REFERENCES usuario(id),
    INDEX idx_accion_afectado (usuario_afectado_id)
);
