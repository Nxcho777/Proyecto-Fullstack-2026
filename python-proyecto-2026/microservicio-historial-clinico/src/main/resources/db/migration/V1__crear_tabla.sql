CREATE TABLE IF NOT EXISTS historiales_clinicos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    paciente_id INTEGER NOT NULL,
    fecha_registro TEXT NOT NULL,
    antecedentes TEXT NOT NULL,
    observaciones TEXT NOT NULL,
    alergias TEXT NOT NULL
);
