CREATE TABLE IF NOT EXISTS notificaciones (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id INTEGER NOT NULL,
    mensaje TEXT NOT NULL,
    canal TEXT NOT NULL,
    estado TEXT NOT NULL,
    fecha_envio TEXT NOT NULL
);
