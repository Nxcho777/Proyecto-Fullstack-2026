UPDATE usuarios SET email = TRIM(email);

CREATE UNIQUE INDEX uk_usuarios_email ON usuarios(email);
CREATE UNIQUE INDEX uk_usuarios_username ON usuarios(username);
