# Microservicios agregados para SaludConecta

Se agregaron 8 microservicios nuevos manteniendo el patrón Controller → Service → Repository, Swagger/OpenAPI, HATEOAS, validaciones, Spring Security básico, Flyway, SQLite y tests unitarios de controller.

## Servicios y puertos

- `microservicio-citas` → puerto `8082` → endpoint `http://localhost:8082/api/citas` → Swagger `http://localhost:8082/swagger-ui.html`
- `microservicio-especialidades` → puerto `8083` → endpoint `http://localhost:8083/api/especialidades` → Swagger `http://localhost:8083/swagger-ui.html`
- `microservicio-recetas` → puerto `8084` → endpoint `http://localhost:8084/api/recetas` → Swagger `http://localhost:8084/swagger-ui.html`
- `microservicio-historial-clinico` → puerto `8085` → endpoint `http://localhost:8085/api/historiales-clinicos` → Swagger `http://localhost:8085/swagger-ui.html`
- `microservicio-diagnosticos` → puerto `8086` → endpoint `http://localhost:8086/api/diagnosticos` → Swagger `http://localhost:8086/swagger-ui.html`
- `microservicio-notificaciones` → puerto `8087` → endpoint `http://localhost:8087/api/notificaciones` → Swagger `http://localhost:8087/swagger-ui.html`
- `microservicio-examenes` → puerto `8088` → endpoint `http://localhost:8088/api/examenes` → Swagger `http://localhost:8088/swagger-ui.html`
- `microservicio-pagos` → puerto `8089` → endpoint `http://localhost:8089/api/pagos` → Swagger `http://localhost:8089/swagger-ui.html`

## Comandos de prueba

Desde cada carpeta de microservicio:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

## Endpoints base por cada microservicio

- `GET /api/<recurso>`: listar registros.
- `GET /api/<recurso>/{id}`: buscar por ID.
- `POST /api/<recurso>`: crear registro.
- `PUT /api/<recurso>/{id}`: actualizar registro.
- `DELETE /api/<recurso>/{id}`: eliminar registro.

## Ejemplos JSON para Postman

### microservicio-citas
```json
{
  "pacienteId": 1,
  "medicoId": 1,
  "fecha": "2026-07-10",
  "hora": "10:30",
  "motivo": "Control general",
  "estado": "AGENDADA"
}
```

### microservicio-especialidades
```json
{
  "nombre": "Cardiología",
  "descripcion": "Atención de enfermedades cardiovasculares",
  "area": "Medicina interna",
  "activa": true
}
```

### microservicio-recetas
```json
{
  "pacienteId": 1,
  "medicoId": 1,
  "medicamento": "Paracetamol",
  "dosis": "500 mg",
  "indicaciones": "Tomar cada 8 horas por 3 días",
  "fechaEmision": "2026-07-10"
}
```

### microservicio-historial-clinico
```json
{
  "pacienteId": 1,
  "fechaRegistro": "2026-07-10",
  "antecedentes": "Hipertensión controlada",
  "observaciones": "Paciente estable",
  "alergias": "Penicilina"
}
```

### microservicio-diagnosticos
```json
{
  "pacienteId": 1,
  "medicoId": 1,
  "descripcion": "Gripe común",
  "gravedad": "LEVE",
  "fecha": "2026-07-10"
}
```

### microservicio-notificaciones
```json
{
  "usuarioId": 1,
  "mensaje": "Su cita fue agendada correctamente",
  "canal": "EMAIL",
  "estado": "ENVIADA",
  "fechaEnvio": "2026-07-10"
}
```

### microservicio-examenes
```json
{
  "pacienteId": 1,
  "tipoExamen": "Hemograma",
  "resultado": "Pendiente",
  "estado": "SOLICITADO",
  "fechaSolicitud": "2026-07-10"
}
```

### microservicio-pagos
```json
{
  "pacienteId": 1,
  "monto": 25000.0,
  "metodoPago": "TARJETA",
  "estado": "PAGADO",
  "fechaPago": "2026-07-10"
}
```
