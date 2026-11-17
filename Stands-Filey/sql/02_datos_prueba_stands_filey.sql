-- ============================================================================
-- Datos de prueba para stands_filey
-- ============================================================================

USE stands_filey;

INSERT INTO usuarios (id, nombre_usuario_editorial, correo, contrasena_hash, salt, telefono) VALUES
(1, 'Editorial Universitaria UADY', 'contacto@editorialuady.mx', 'hash_simulado_123', 'salt_abc', '9991234567'),
(2, 'Fondo de Cultura Económica', 'ferias@fce.com.mx', 'hash_simulado_456', 'salt_def', '5559876543');

INSERT INTO stands (id, largo, ancho, precio, estado, transferencia_id) VALUES
('ST-A01', 3.0, 3.0, 15000.00, 'RESERVADO', NULL),
('ST-A02', 4.0, 3.0, 20000.00, 'DISPONIBLE', NULL),
('ST-B01', 6.0, 4.0, 35000.00, 'BLOQUEADO', NULL);

INSERT INTO reservas (id, usuario_id, stand_id, fecha_reserva, fecha_limite, monto_pagado, estado) VALUES
(1, 1, 'ST-A01', '2026-11-01', '2026-11-25', 5000.00, 'ACTIVA');

INSERT INTO transferencias (id, codigo_acceso, estado, fecha_acceso, usuario_id, reserva_id, stand_id) VALUES
(1, 'TRF-2026-9988', 'PENDIENTE', '2026-11-15 10:30:00', 2, NULL, 'ST-B01');

UPDATE stands SET transferencia_id = 1 WHERE id = 'ST-B01';

INSERT INTO pagos (id, reserva_id, monto, fecha, tipo, confirmado) VALUES
(1, 1, 5000.00, '2026-11-02 14:20:00', 'TRANSFERENCIA', 1);

INSERT INTO notificaciones (id, usuario_id, tipo, asunto, mensaje, fecha, leida) VALUES
(1, 1, 'RESERVA', 'Reserva registrada', 'Su reserva para el stand ST-A01 ha sido registrada.', '2026-11-01 09:00:00', 0);
