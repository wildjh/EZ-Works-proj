ALTER TABLE usuario
    ADD COLUMN foto_perfil_url VARCHAR(500) NULL AFTER telefono;

CREATE TABLE evidencia_trabajo (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    perfil_ayudante_id BIGINT UNSIGNED NOT NULL,
    url_archivo VARCHAR(500) NOT NULL,
    tipo ENUM('FOTO', 'DOCUMENTO') NOT NULL DEFAULT 'FOTO',
    descripcion VARCHAR(200),
    subido_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    FOREIGN KEY (perfil_ayudante_id) REFERENCES perfil_ayudante(id),
    INDEX idx_evidencia_ayudante (perfil_ayudante_id)
);
