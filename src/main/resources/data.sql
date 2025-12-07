--------------------------------
-- USUARIOS (Rol: GERENTE/EMPLEADO)
--------------------------------
INSERT IGNORE INTO usuario (id, username, password_hash, rol, activo, foto_perfil) VALUES
(1,  'gerente1',  '{noop}passgerente1',  'GERENTE',  1, NULL),
(2,  'gerente2',  '{noop}passgerente2',  'GERENTE',  1, NULL),
(3,  'empleado1', '{noop}passempleado1', 'EMPLEADO', 1, NULL),
(4,  'empleado2', '{noop}passempleado2', 'EMPLEADO', 1, NULL),
(5,  'empleado3', '{noop}passempleado3', 'EMPLEADO', 1, NULL),
(6,  'empleado4', '{noop}passempleado4', 'EMPLEADO', 1, NULL),
(7,  'empleado5', '{noop}passempleado5', 'EMPLEADO', 1, NULL),
(8,  'empleado6', '{noop}passempleado6', 'EMPLEADO', 1, NULL),
(9,  'empleado7', '{noop}passempleado7', 'EMPLEADO', 1, NULL),
(10, 'empleado8', '{noop}passempleado8', 'EMPLEADO', 1, NULL),
(11, 'empleado9', '{noop}passempleado9', 'EMPLEADO', 1, NULL);

--------------------------------
-- GERENTES
--------------------------------
INSERT IGNORE INTO gerente (id, nombre, email, telefono, usuario_id) VALUES
(1, 'Carlos García', 'carlos.gerente@agro.com', '600111222', 1),
(2, 'Ana Martínez',  'ana.gerente@agro.com',    '600333444', 2);

--------------------------------
-- EMPLEADOS
--------------------------------
INSERT IGNORE INTO empleado (id, dni, nombre, activo, usuario_id) VALUES
(1, '11111111A', 'Juan Pérez',    1, 3),
(2, '22222222B', 'María López',   1, 4),
(3, '33333333C', 'Pedro Sánchez', 1, 5);

--------------------------------
-- FINCAS
--------------------------------
INSERT IGNORE INTO finca (id, nombre, estado, ciudad, provincia, area, gerente_id, latitud, longitud) VALUES
(1, 'Finca El Olivar',    'SEMBRADA',      'Mérida',    'Badajoz',  12.50, 1, 38.9170, -6.3430),
(2, 'Finca Los Naranjos', 'MANTENIMIENTO', 'Don Benito','Badajoz',  8.75,  1, 38.9569, -5.8616),
(3, 'Finca La Viña',      'LISTA_COSECHA', 'Cáceres',   'Cáceres',  15.20, 1, 39.4760, -6.3710);


--------------------------------
-- CONTRATOS
--------------------------------
INSERT IGNORE INTO contrato (
    id, tipo, fecha_inicio, fecha_fin, salario_base, tarifa_hora, empleado_id
) VALUES
(1, 'INDEFINIDO',       '2024-01-01', NULL,         1500.00, 10.50, 1),
(2, 'TEMPORAL',         '2024-03-01', '2024-09-30', 1300.00,  9.50, 2),
(3, 'FIJO_DISCONTINUO', '2024-02-15', NULL,         1200.00,  8.75, 3);

--------------------------------
-- ASIGNACIONES (Empleado ↔ Finca)
--------------------------------
INSERT IGNORE INTO asignacion (
    id, fecha_inicio, fecha_fin, activa, empleado_id, finca_id
) VALUES
(1, '2024-03-01', NULL,         1, 1, 1),
(2, '2024-04-01', '2024-06-30', 0, 2, 2),
(3, '2024-05-15', NULL,         1, 3, 3);

--------------------------------
-- TAREAS
--------------------------------
INSERT IGNORE INTO tarea (
    id, fecha, tipo, horas, empleado_id, finca_id, realizada
) VALUES
(1, '2024-05-01', 'Riego',       4.00, 1, 1, 0),
(2, '2024-05-02', 'Poda',        3.50, 1, 1, 0),
(3, '2024-05-03', 'Cosecha',     6.00, 2, 2, 0),
(4, '2024-05-04', 'Fertilizado', 5.00, 3, 3, 0),
(5, '2024-05-05', 'Riego',       2.50, 2, 2, 0);

--------------------------------
-- FICHAJES
--------------------------------
INSERT IGNORE INTO fichaje (
    id, inicio, fin, estado, empleado_id, finca_id
) VALUES
(1, '2024-05-01 08:00:00', '2024-05-01 12:00:00', 'CERRADO', 1, 1),
(2, '2024-05-01 14:00:00', '2024-05-01 18:00:00', 'CERRADO', 1, 1),
(3, '2024-05-02 08:30:00', '2024-05-02 13:00:00', 'CERRADO', 2, 2),
(4, '2024-05-03 09:00:00', NULL,                  'ABIERTO', 3, 3);
