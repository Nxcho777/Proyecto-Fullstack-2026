# Revisión para entrega

Este repositorio contiene dos proyectos Spring Boot:

1. `SaludConecta`: aplicación principal.
2. `microservicio-usuarios`: microservicio para gestión de usuarios.

## Cumplimiento de la consigna

- Aplicación principal con mínimo 3 entidades:
  - `Paciente`
  - `Medico`
  - `Tratamiento`
- Persistencia con SQLite:
  - `jdbc:sqlite:saludconecta.db`
- Migraciones Flyway en aplicación principal:
  - `V1__creacion_tablas.sql`
  - `V2__insertar_datos.sql`
- Microservicio de usuarios:
  - Clase `Usuario` con 5 atributos: `id`, `username`, `password`, `email`, `rol`.
  - Persistencia con SQLite: `jdbc:sqlite:usuarios.db`.
  - Migraciones Flyway:
    - `V1__crear_tabla_usuarios.sql`
    - `V2__insertar_usuarios.sql`
  - Carga inicial de 5 usuarios.
- Integración API desde aplicación principal hacia microservicio:
  - `SaludConecta` usa `WebClient` apuntando a `http://localhost:8081/api/usuarios`.
  - Endpoint de prueba: `GET http://localhost:8080/api/test-conexion/verificar/admin`.

## Cómo probar

Primero ejecutar el microservicio de usuarios:

```bash
cd microservicio-usuarios
./mvnw spring-boot:run
```

Luego, en otra terminal, ejecutar la aplicación principal:

```bash
cd SaludConecta
./mvnw spring-boot:run
```

Probar usuarios:

```text
http://localhost:8081/api/usuarios/existe/admin
http://localhost:8081/api/usuarios/existe/dr_perez
```

Probar integración desde SaludConecta:

```text
http://localhost:8080/api/test-conexion/verificar/admin
http://localhost:8080/api/test-conexion/verificar/no_existe
```
