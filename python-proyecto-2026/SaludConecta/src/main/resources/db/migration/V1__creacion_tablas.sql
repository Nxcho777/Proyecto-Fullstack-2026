CREATE TABLE medicos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre VARCHAR(255),
    apellido VARCHAR(255),
    rut VARCHAR(255),
    especialidad VARCHAR(255),
    email VARCHAR(255)
);

CREATE TABLE pacientes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre VARCHAR(255),
    apellido VARCHAR(255),
    rut VARCHAR(255),
    email VARCHAR(255),
    telefono VARCHAR(255)
);

CREATE TABLE tratamientos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    diagnostico VARCHAR(255),
    medicamento VARCHAR(255),
    dosis VARCHAR(255),
    duracion_dias INTEGER
);
