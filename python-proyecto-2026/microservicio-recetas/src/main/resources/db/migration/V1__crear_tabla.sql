CREATE TABLE IF NOT EXISTS recetas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    paciente_id INTEGER NOT NULL,
    medico_id INTEGER NOT NULL,
    medicamento TEXT NOT NULL,
    dosis TEXT NOT NULL,
    indicaciones TEXT NOT NULL,
    fecha_emision TEXT NOT NULL
);
