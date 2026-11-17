-- ============================================================================
-- Base de Datos: stands_filey
-- Motor: MySQL / MariaDB
-- Generado a partir de los modelos y DAOs de StandsFiley_Reorganizado
-- ============================================================================

CREATE DATABASE IF NOT EXISTS stands_filey
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE stands_filey;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS pagos;
DROP TABLE IF EXISTS notificaciones;
DROP TABLE IF EXISTS transferencias;
DROP TABLE IF EXISTS reservas;
DROP TABLE IF EXISTS stands;
DROP TABLE IF EXISTS usuarios;

-- ----------------------------------------------------------------------------
-- 1. Tabla: usuarios
-- ----------------------------------------------------------------------------
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario_editorial VARCHAR(150) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    contrasena_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    telefono VARCHAR(30) NULL
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 2. Tabla: stands
-- ----------------------------------------------------------------------------
CREATE TABLE stands (
    id VARCHAR(50) PRIMARY KEY,
    largo DOUBLE NOT NULL,
    ancho DOUBLE NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    estado ENUM('DISPONIBLE', 'RESERVADO', 'OCUPADO', 'BLOQUEADO') NOT NULL DEFAULT 'DISPONIBLE',
    transferencia_id INT NULL
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 3. Tabla: reservas
-- ----------------------------------------------------------------------------
CREATE TABLE reservas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    stand_id VARCHAR(50) NOT NULL,
    fecha_reserva DATE NOT NULL,
    fecha_limite DATE NOT NULL,
    monto_pagado DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    estado ENUM('ACTIVA', 'COMPLETADA', 'VENCIDA') NOT NULL DEFAULT 'ACTIVA',
    CONSTRAINT fk_reservas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_reservas_stand FOREIGN KEY (stand_id) REFERENCES stands(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 4. Tabla: transferencias
-- ----------------------------------------------------------------------------
CREATE TABLE transferencias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_acceso VARCHAR(100) NOT NULL UNIQUE,
    estado ENUM('PENDIENTE', 'ACEPTADA', 'RECHAZADA', 'EXPIRADA') NOT NULL DEFAULT 'PENDIENTE',
    fecha_acceso DATETIME NULL,
    usuario_id INT NOT NULL,
    reserva_id INT NULL,
    stand_id VARCHAR(50) NOT NULL,
    CONSTRAINT fk_transferencias_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_transferencias_reserva FOREIGN KEY (reserva_id) REFERENCES reservas(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_transferencias_stand FOREIGN KEY (stand_id) REFERENCES stands(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

ALTER TABLE stands
    ADD CONSTRAINT fk_stands_transferencia FOREIGN KEY (transferencia_id) REFERENCES transferencias(id)
        ON UPDATE CASCADE ON DELETE SET NULL;

-- ----------------------------------------------------------------------------
-- 5. Tabla: notificaciones
-- ----------------------------------------------------------------------------
CREATE TABLE notificaciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    asunto VARCHAR(200) NOT NULL,
    mensaje TEXT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    leida TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_notificaciones_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 6. Tabla: pagos
-- ----------------------------------------------------------------------------
CREATE TABLE pagos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reserva_id INT NOT NULL,
    monto DECIMAL(10, 2) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo VARCHAR(50) NOT NULL,
    confirmado TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_pagos_reserva FOREIGN KEY (reserva_id) REFERENCES reservas(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;
