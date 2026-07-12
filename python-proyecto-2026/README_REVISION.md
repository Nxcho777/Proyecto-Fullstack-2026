# Proyecto Fullstack 2026 - SaludConecta

El proyecto conserva únicamente los dos microservicios originales y el API Gateway:

1. `SaludConecta` - aplicación principal, puerto `8080`.
2. `microservicio-usuarios` - gestión y autenticación de usuarios, puerto `8081`.
3. `api-gateway` - punto de entrada único, puerto `8090`.

## Arquitectura

El código mantiene el patrón CSR:

- `controller`: recibe solicitudes HTTP y devuelve códigos de estado.
- `service`: contiene reglas de negocio, validaciones, logs y comunicación remota.
- `repository`: administra la persistencia mediante Spring Data JPA.

Las entidades originales se mantienen:

- SaludConecta: `Paciente`, `Medico` y `Tratamiento`.
- Usuarios: `Usuario`.

## Orden para levantar el proyecto

Abrir tres terminales desde `python-proyecto-2026`.

```powershell
cd microservicio-usuarios
.\mvnw.cmd spring-boot:run
```

```powershell
cd SaludConecta
.\mvnw.cmd spring-boot:run
```

```powershell
cd api-gateway
.\mvnw.cmd spring-boot:run
```

También se puede ejecutar `run_Proyecto.ps1` para abrir los tres procesos.

La ejecución normal de `SaludConecta` utiliza Flyway. Para cargar la base independiente de demostración con DataFaker se puede iniciar explícitamente con:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=datafaker
```

## Rutas del API Gateway

El gateway contiene solamente dos definiciones de ruta:

- SaludConecta: `/api/pacientes/**`, `/api/medicos/**`, `/api/tratamientos/**` y `/api/test-conexion/**`.
- Usuarios: `/api/usuarios/**` y `/api/auth/**`.

Ejemplos por gateway:

```text
GET  http://localhost:8090/api/pacientes
GET  http://localhost:8090/api/medicos
GET  http://localhost:8090/api/tratamientos
POST http://localhost:8090/api/auth/login
GET  http://localhost:8090/api/usuarios
```

## Comunicación REST distribuida

`SaludConecta` consulta a `microservicio-usuarios` mediante `WebClient` y un DTO llamado `UsuarioExistenciaResponse`.

```text
GET http://localhost:8090/api/test-conexion/verificar/admin@saludconecta.cl
```

La URL remota se configura en:

```properties
microservicio.usuarios.url=http://localhost:8081/api/usuarios
```

Los errores de conexión se controlan mediante `MicroservicioNoDisponibleException` y se devuelven como JSON con código `503`.

## Swagger / OpenAPI

```text
SaludConecta:          http://localhost:8080/doc/swagger-ui.html
microservicio-usuarios: http://localhost:8081/doc/swagger-ui.html
```

## Pruebas y cobertura

Cada proyecto configura JaCoCo para medir la capa `service`, que corresponde a la lógica de negocio evaluada por la rúbrica.

```powershell
cd SaludConecta
.\mvnw.cmd clean verify
```

```powershell
cd microservicio-usuarios
.\mvnw.cmd clean verify
```

El reporte HTML queda en:

```text
target/site/jacoco/index.html
```

La verificación exige al menos 80% de cobertura de líneas en la capa `service`.

## Relación con la rúbrica ET

- Endpoints REST: rutas semánticas, `GET`, `POST`, `PUT`, `DELETE`, códigos `200`, `201`, `204`, `400`, `401`, `404`, `409` y `503`.
- Patrón CSR: separación entre Controller, Service y Repository.
- Validación: Bean Validation en entidades, controladores y servicios con `@Valid` y `@Validated`.
- Errores: `GlobalExceptionHandler` con respuestas JSON uniformes.
- Base de datos y CRUD: JPA, SQLite, Flyway y CRUD completo para las entidades.
- Reglas de negocio: prevención de RUT, email y username duplicados.
- Logs: SLF4J en servicios, autenticación y manejo de errores.
- Comunicación REST: WebClient, DTO y control de fallos remotos.
- Pruebas unitarias: JUnit 5, Mockito y JaCoCo.
- Documentación: Swagger/OpenAPI con modelos, operaciones y códigos reales.

Los criterios de GitHub, Trello y aporte personal requieren evidencia real del equipo: commits progresivos, tarjetas asignadas y participación individual; no se pueden reemplazar solamente con código.
