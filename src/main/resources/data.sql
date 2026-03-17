-- Insertar datos comunes en el padre
INSERT INTO formatos (tipo_formato, fecha_creacion, titulo, director_trabajo, objetivo_general) 
VALUES ('PP', '2026-03-01', 'Desarrollo API REST', 'Ing. Carlos', 'Crear microservicios');

-- Insertar datos específicos en la tabla hija correspondiente (usando el ID 1)
INSERT INTO formatos_pp (formato_id, nombre_estudiante, codigo_estudiante, asesor_organizacion, tiene_carta_aceptacion) 
VALUES (1, 'Jonas Hurtado', 104000, 'Ing. Maria', true);

-- Insertar objetivos
INSERT INTO objetivos (formato_id, objetivo) VALUES (1, 'Configurar Spring Boot');