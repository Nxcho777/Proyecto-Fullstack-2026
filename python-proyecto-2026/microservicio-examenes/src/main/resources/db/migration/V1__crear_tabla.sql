CREATE TABLE IF NOT EXISTS examenes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    paciente_id INTEGER NOT NULL,
    tipo_examen TEXT NOT NULL,
    resultado TEXT NOT NULL,
    estado TEXT NOT NULL,
    fecha_solicitud TEXT NOT NULL
);
