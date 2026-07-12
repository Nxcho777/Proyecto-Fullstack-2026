# Validación de la versión ET

## Comprobaciones realizadas

- Solo permanecen `SaludConecta`, `microservicio-usuarios` y `api-gateway`.
- El API Gateway tiene exactamente dos rutas, dirigidas a los puertos 8080 y 8081.
- No quedan referencias activas a los ocho microservicios eliminados ni a sus puertos 8082-8089.
- Los tres archivos `pom.xml` tienen estructura XML válida.
- `application.yml` tiene estructura YAML válida.
- Las cinco migraciones de SaludConecta y las tres de usuarios se aplicaron correctamente sobre bases SQLite temporales.
- El código Java no presenta errores sintácticos detectables por el parser de `javac`.
- `git diff --check` no detecta espacios finales ni conflictos de formato.

## Verificación que debe ejecutarse en el equipo local

Este entorno no pudo descargar Maven ni sus dependencias, por lo que no fue posible ejecutar una compilación Maven completa. En un equipo con conexión a internet, ejecutar:

```powershell
cd SaludConecta
.\mvnw.cmd clean verify
```

```powershell
cd microservicio-usuarios
.\mvnw.cmd clean verify
```

```powershell
cd api-gateway
.\mvnw.cmd clean test
```

Los dos primeros comandos generan el informe JaCoCo en `target/site/jacoco/index.html` y comprueban el mínimo configurado de 80% en la capa `service`.

## Evidencias externas pendientes

Los criterios de GitHub, Trello y aporte personal necesitan evidencia real del equipo. Se deben conservar y mostrar commits progresivos, ramas o pull requests, tarjetas asignadas y participación individual.
