CREATE TABLE pacientes_nueva (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    rut VARCHAR(255) NOT NULL,
    correo VARCHAR(255) NOT NULL,
    telefono VARCHAR(255) NOT NULL
);

INSERT INTO pacientes_nueva (id, nombre, apellido, rut, correo, telefono)
SELECT id, nombre, apellido, rut, COALESCE(correo, email), telefono
FROM pacientes;

DROP TABLE pacientes;
ALTER TABLE pacientes_nueva RENAME TO pacientes;

CREATE UNIQUE INDEX uk_pacientes_rut ON pacientes(rut);
CREATE UNIQUE INDEX uk_pacientes_correo ON pacientes(correo);
