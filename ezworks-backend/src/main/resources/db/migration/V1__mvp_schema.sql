CREATE TABLE rol (
    id TINYINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(80) NOT NULL
);

INSERT INTO rol (codigo, nombre) VALUES
    ('EMPLEADOR', 'Empleador'),
    ('AYUDANTE', 'Ayudante'),
    ('ADMIN', 'Administrador');

CREATE TABLE usuario (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    estado_cuenta ENUM('ACTIVO','SUSPENDIDO','BANEADO','INHABILITADO_DEUDA') NOT NULL DEFAULT 'ACTIVO',
    creado_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    actualizado_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
);

CREATE TABLE usuario_rol (
    usuario_id BIGINT UNSIGNED NOT NULL,
    rol_id TINYINT UNSIGNED NOT NULL,
    asignado_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (rol_id) REFERENCES rol(id)
);

CREATE TABLE perfil_empleador (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT UNSIGNED NOT NULL UNIQUE,
    calificacion_promedio DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    total_resenas INT UNSIGNED NOT NULL DEFAULT 0,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE perfil_ayudante (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT UNSIGNED NOT NULL UNIQUE,
    bio TEXT,
    calificacion_promedio DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    total_resenas INT UNSIGNED NOT NULL DEFAULT 0,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE aceptacion_terminos (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT UNSIGNED NOT NULL,
    version_terminos VARCHAR(20) NOT NULL,
    ip_origen VARCHAR(45),
    aceptado_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE refresh_token (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT UNSIGNED NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expira_en DATETIME(3) NOT NULL,
    revocado BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE categoria (
    id SMALLINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
);

CREATE TABLE requerimiento (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    empleador_id BIGINT UNSIGNED NOT NULL,
    categoria_id SMALLINT UNSIGNED NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT NOT NULL,
    remuneracion DECIMAL(12,2) NOT NULL,
    estado ENUM('BORRADOR','PUBLICADO','EN_MATCH','FINALIZADO','CANCELADO') NOT NULL DEFAULT 'BORRADOR',
    zona_aproximada VARCHAR(200),
    latitud_aprox DECIMAL(10,7),
    longitud_aprox DECIMAL(10,7),
    latitud_exacta DECIMAL(10,7),
    longitud_exacta DECIMAL(10,7),
    direccion_exacta VARCHAR(300),
    publicado_en DATETIME(3),
    actualizado_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    finalizado_en DATETIME(3),
    FOREIGN KEY (empleador_id) REFERENCES perfil_empleador(id),
    FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    INDEX idx_req_estado_cat (estado, categoria_id),
    INDEX idx_req_empleador (empleador_id)
);

CREATE TABLE postulacion (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    requerimiento_id BIGINT UNSIGNED NOT NULL,
    ayudante_id BIGINT UNSIGNED NOT NULL,
    mensaje_presentacion VARCHAR(500),
    estado ENUM('PENDIENTE','ACEPTADA','RECHAZADA','RETIRADA') NOT NULL DEFAULT 'PENDIENTE',
    creado_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_postulacion (requerimiento_id, ayudante_id),
    FOREIGN KEY (requerimiento_id) REFERENCES requerimiento(id),
    FOREIGN KEY (ayudante_id) REFERENCES perfil_ayudante(id)
);

CREATE TABLE emparejamiento (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    requerimiento_id BIGINT UNSIGNED NOT NULL UNIQUE,
    empleador_id BIGINT UNSIGNED NOT NULL,
    ayudante_id BIGINT UNSIGNED NOT NULL,
    postulacion_id BIGINT UNSIGNED NOT NULL UNIQUE,
    establecido_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    finalizado_en DATETIME(3),
    FOREIGN KEY (requerimiento_id) REFERENCES requerimiento(id),
    FOREIGN KEY (empleador_id) REFERENCES perfil_empleador(id),
    FOREIGN KEY (ayudante_id) REFERENCES perfil_ayudante(id),
    FOREIGN KEY (postulacion_id) REFERENCES postulacion(id)
);

CREATE TABLE conversacion (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    emparejamiento_id BIGINT UNSIGNED NOT NULL UNIQUE,
    abierta_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    FOREIGN KEY (emparejamiento_id) REFERENCES emparejamiento(id)
);

CREATE TABLE mensaje (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    conversacion_id BIGINT UNSIGNED NOT NULL,
    emisor_usuario_id BIGINT UNSIGNED NOT NULL,
    contenido TEXT NOT NULL,
    leido BOOLEAN NOT NULL DEFAULT FALSE,
    enviado_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    FOREIGN KEY (conversacion_id) REFERENCES conversacion(id),
    FOREIGN KEY (emisor_usuario_id) REFERENCES usuario(id),
    INDEX idx_mensaje_conv (conversacion_id, enviado_en)
);
