-- ============================
-- AgroManager: Carga de datos
-- ============================

SET
FOREIGN_KEY_CHECKS = 0;

-- Limpieza (orden por dependencias)
TRUNCATE TABLE asignacion;
TRUNCATE TABLE contrato;
TRUNCATE TABLE fichaje;
TRUNCATE TABLE tarea;
TRUNCATE TABLE nomina;
TRUNCATE TABLE finca;
TRUNCATE TABLE empleado;
TRUNCATE TABLE gerente;
TRUNCATE TABLE usuario;

SET
FOREIGN_KEY_CHECKS = 1;

-- ============================
-- USUARIOS (roles y acceso)
-- ============================
-- id, username, password_hash (dummy), rol, activo
INSERT INTO usuario (id, username, password_hash, rol, activo)
VALUES (1, 'gerente1', '$2a$10$hash_demo_gerente1', 'GERENTE', 1),
       (2, 'emp1', '$2a$10$hash_demo_emp1', 'EMPLEADO', 1),
       (3, 'emp2', '$2a$10$hash_demo_emp2', 'EMPLEADO', 1),
       (4, 'emp3', '$2a$10$hash_demo_emp3', 'EMPLEADO', 1);

-- ============================
-- GERENTE
-- ============================
-- id, nombre, email, telefono, usuario_id
INSERT INTO gerente (id, nombre, email, telefono, usuario_id)
VALUES (1, 'Laura Campos', 'laura.campos@agro.local', '+34 600 111 222', 1);

-- ============================
-- EMPLEADOS
-- ============================
-- id, dni, nombre, activo, usuario_id
INSERT INTO empleado (id, dni, nombre, activo, usuario_id)
VALUES (1, '11111111A', 'Diego Pérez', 1, 2),
       (2, '22222222B', 'Hugo Martín', 1, 3),
       (3, '33333333C', 'Ana López', 1, 4);

-- ============================
-- FINCAS
-- ============================
-- id, nombre, estado, gerente_id
INSERT INTO finca (id, nombre, estado, gerente_id)
VALUES (1, 'Finca Encinas', 'SEMBRADA', 1),
       (2, 'Finca Olivares', 'MANTENIMIENTO', 1),
       (3, 'Finca Rivera', 'BARBECHO', 1);

-- ============================
-- CONTRATOS
-- ============================
-- id, tipo, fecha_inicio, fecha_fin, salario_base, tarifa_hora, empleado_id
INSERT INTO contrato (id, tipo, fecha_inicio, fecha_fin, salario_base, tarifa_hora, empleado_id)
VALUES (1, 'INDEFINIDO', '2024-01-01', NULL, 1200.00, 12.50, 1),
       (2, 'TEMPORAL', '2025-01-01', '2025-12-31', 1100.00, 11.00, 2),
       (3, 'FIJO_DISCONTINUO', '2024-09-01', '2026-09-01', 1000.00, 10.00, 3);

-- ============================
-- ASIGNACIONES
-- ============================
-- id, fecha_inicio, fecha_fin, activa, empleado_id, finca_id
INSERT INTO asignacion (id, fecha_inicio, fecha_fin, activa, empleado_id, finca_id)
VALUES (1, '2025-01-10', NULL, 1, 1, 1),
       (2, '2025-03-01', '2025-06-30', 0, 2, 2),
       (3, '2025-07-01', NULL, 1, 2, 2),
       (4, '2025-05-15', NULL, 1, 3, 3);

-- ============================
-- FICHAJES (horario)
-- ============================
-- id, inicio, fin, estado, empleado_id, finca_id
-- Usamos timestamps relativos para que haya uno activo y otros cerrados
INSERT INTO fichaje (id, inicio, fin, estado, empleado_id, finca_id)
VALUES (1, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY + INTERVAL 8 HOUR, 'CERRADO', 1, 1),
       (2, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY + INTERVAL 7 HOUR, 'CERRADO', 1, 1),
       (3, NOW() - INTERVAL 3 HOUR, NULL, 'ABIERTO', 1, 1), -- activo

       (4, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY + INTERVAL 6 HOUR, 'CERRADO', 2, 2),
       (5, NOW() - INTERVAL 1 HOUR, NULL, 'ABIERTO', 2, 2), -- activo

       (6, NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY + INTERVAL 5 HOUR, 'CERRADO', 3, 3);

-- ============================
-- TAREAS (productividad)
-- ============================
-- id, fecha, tipo, horas, empleado_id, finca_id
INSERT INTO tarea (id, fecha, tipo, horas, empleado_id, finca_id)
VALUES (1, '2025-10-25', 'Siembra', 6.0, 1, 1),
       (2, '2025-10-26', 'Riego', 4.5, 1, 1),
       (3, '2025-10-27', 'Mantenimiento', 3.0, 1, 1),

       (4, '2025-10-20', 'Poda', 5.0, 2, 2),
       (5, '2025-10-28', 'Riego', 6.5, 2, 2),

       (6, '2025-10-18', 'Limpieza', 4.0, 3, 3),
       (7, '2025-10-29', 'Revisión', 2.5, 3, 3);

-- ============================
-- NOMINAS (cierre mensual)
-- ============================
-- id, periodo_inicio, periodo_fin, total_bruto, estado, empleado_id
INSERT INTO nomina (id, periodo_inicio, periodo_fin, total_bruto, estado, empleado_id)
VALUES (1, '2025-09-01', '2025-09-30', 1800.00, 'PAGADA', 1),
       (2, '2025-10-01', '2025-10-31', 1950.00, 'GENERADA', 1),

       (3, '2025-09-01', '2025-09-30', 1600.00, 'PAGADA', 2),
       (4, '2025-10-01', '2025-10-31', 1700.00, 'GENERADA', 2),

       (5, '2025-09-01', '2025-09-30', 1400.00, 'PAGADA', 3),
       (6, '2025-10-01', '2025-10-31', 1500.00, 'GENERADA', 3);

-- ============================
-- Extras útiles para pruebas
-- ============================
-- Búsqueda por nombre (Finca), estados (Finca/Nómina), solapamientos (Asignación),
-- contrato vigente (Contrato), horas por rango (Tarea), fichaje activo (Fichaje)

-- Fin de la carga
