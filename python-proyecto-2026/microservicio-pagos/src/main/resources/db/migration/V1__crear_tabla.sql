CREATE TABLE IF NOT EXISTS pagos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    paciente_id INTEGER NOT NULL,
    monto REAL NOT NULL,
    metodo_pago TEXT NOT NULL,
    estado TEXT NOT NULL,
    fecha_pago TEXT NOT NULL
);
