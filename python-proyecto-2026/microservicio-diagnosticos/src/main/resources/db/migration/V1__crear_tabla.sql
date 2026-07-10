CREATE TABLE IF NOT EXISTS diagnosticos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    paciente_id INTEGER NOT NULL,
    medico_id INTEGER NOT NULL,
    descripcion TEXT NOT NULL,
    gravedad TEXT NOT NULL,
    fecha TEXT NOT NULL
);
