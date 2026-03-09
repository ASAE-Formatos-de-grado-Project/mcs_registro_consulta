 -- Insertar un formato PP de prueba
INSERT INTO formatos (tipo_formato, fecha_creacion, titulo, director_trabajo, objetivo_general, nombre_estudiante, codigo_estudiante, asesor_organizacion, tiene_carta_aceptacion) 
VALUES ('PP', '2026-03-01', 'Desarrollo API REST', 'Ing. Carlos', 'Crear microservicios', 'Jonas Hurtado', 104000, 'Ing. Maria', true);

INSERT INTO objetivos (formato_id, objetivo) VALUES (1, 'Configurar Spring Boot');
INSERT INTO objetivos (formato_id, objetivo) VALUES (1, 'Crear base de datos H2');