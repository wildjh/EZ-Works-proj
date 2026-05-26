ALTER TABLE usuario
    ADD COLUMN saldo_deuda_acumulado DECIMAL(12, 2) NOT NULL DEFAULT 0.00 AFTER estado_cuenta;

CREATE TABLE configuracion_plataforma (
    clave VARCHAR(50) PRIMARY KEY,
    valor DECIMAL(12, 2) NOT NULL,
    descripcion VARCHAR(200)
);

INSERT INTO configuracion_plataforma (clave, valor, descripcion) VALUES
    ('comision_por_match', 5000.00, 'Comisión por match confirmado (RF-16)'),
    ('limite_deuda_maxima', 50000.00, 'Deuda máxima antes de inhabilitar cuenta (RF-19)');

CREATE TABLE metodo_pago (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT UNSIGNED NOT NULL,
    tipo ENUM('TARJETA', 'NEQUI', 'DAVIPLATA', 'OTRO') NOT NULL,
    alias VARCHAR(80) NOT NULL,
    ultimos_cuatro VARCHAR(4),
    predeterminado TINYINT(1) NOT NULL DEFAULT 0,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    creado_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    INDEX idx_metodo_pago_usuario (usuario_id)
);

CREATE TABLE deuda_plataforma (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT UNSIGNED NOT NULL,
    emparejamiento_id BIGINT UNSIGNED NOT NULL,
    monto DECIMAL(12, 2) NOT NULL,
    estado ENUM('PENDIENTE', 'PAGADA', 'VENCIDA') NOT NULL DEFAULT 'PENDIENTE',
    descripcion VARCHAR(200) NOT NULL,
    creado_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    pagada_en DATETIME(3),
    FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    FOREIGN KEY (emparejamiento_id) REFERENCES emparejamiento (id),
    UNIQUE KEY uk_deuda_emparejamiento (emparejamiento_id),
    INDEX idx_deuda_usuario_estado (usuario_id, estado)
);

CREATE TABLE transaccion (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT UNSIGNED NOT NULL,
    deuda_id BIGINT UNSIGNED,
    metodo_pago_id BIGINT UNSIGNED,
    tipo ENUM('COMISION_MATCH', 'PAGO_DEUDA', 'PAGO_TRABAJO') NOT NULL,
    monto DECIMAL(12, 2) NOT NULL,
    estado ENUM('PENDIENTE', 'COMPLETADA', 'FALLIDA') NOT NULL DEFAULT 'PENDIENTE',
    referencia VARCHAR(100),
    creado_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    completada_en DATETIME(3),
    FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    FOREIGN KEY (deuda_id) REFERENCES deuda_plataforma (id),
    FOREIGN KEY (metodo_pago_id) REFERENCES metodo_pago (id),
    INDEX idx_transaccion_usuario (usuario_id)
);
