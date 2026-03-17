DROP TABLE IF EXISTS objetivos;
DROP TABLE IF EXISTS formatos_ti;
DROP TABLE IF EXISTS formatos_pp;
DROP TABLE IF EXISTS formatos;


CREATE TABLE formatos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_formato VARCHAR(2) NOT NULL, 
    fecha_creacion DATE NOT NULL,
    titulo VARCHAR(255),
    director_trabajo VARCHAR(255),
    objetivo_general VARCHAR(255)
);

CREATE TABLE formatos_ti (
    formato_id INT PRIMARY KEY,
    nombre_estudiante1 VARCHAR(255),
    nombre_estudiante2 VARCHAR(255),
    codigo_estudiante1 INT,
    codigo_estudiante2 INT,
    FOREIGN KEY (formato_id) REFERENCES formatos(id) ON DELETE CASCADE
);


CREATE TABLE formatos_pp (
    formato_id INT PRIMARY KEY,
    nombre_estudiante VARCHAR(255),
    codigo_estudiante INT,
    asesor_organizacion VARCHAR(255),
    tiene_carta_aceptacion BOOLEAN,
    FOREIGN KEY (formato_id) REFERENCES formatos(id) ON DELETE CASCADE
);


CREATE TABLE objetivos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    formato_id INT,
    objetivo VARCHAR(500),
    FOREIGN KEY (formato_id) REFERENCES formatos(id) ON DELETE CASCADE
);