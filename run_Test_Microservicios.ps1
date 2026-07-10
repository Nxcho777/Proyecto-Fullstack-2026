$servicios = @(
    "microservicio-usuarios",
    "SaludConecta",
    "microservicio-citas",
    "microservicio-especialidades",
    "microservicio-recetas",
    "microservicio-historial-clinico",
    "microservicio-diagnosticos",
    "microservicio-notificaciones",
    "microservicio-examenes",
    "microservicio-pagos"
)

foreach ($servicio in $servicios) {
    Write-Host "=======================================" -ForegroundColor Cyan
    Write-Host "Ejecutando tests en $servicio" -ForegroundColor Cyan
    Write-Host "=======================================" -ForegroundColor Cyan
    Push-Location $servicio
    .\mvnw.cmd clean test
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Fallaron los tests en $servicio" -ForegroundColor Red
        Pop-Location
        exit $LASTEXITCODE
    }
    Pop-Location
}

Write-Host "Todos los tests terminaron correctamente." -ForegroundColor Green
