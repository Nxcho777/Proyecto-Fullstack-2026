$base = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "Iniciando microservicio-usuarios en el puerto 8081..."
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$base\microservicio-usuarios'; .\mvnw.cmd spring-boot:run"

Start-Sleep -Seconds 3

Write-Host "Iniciando SaludConecta en el puerto 8080..."
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$base\SaludConecta'; .\mvnw.cmd spring-boot:run"

Start-Sleep -Seconds 3

Write-Host "Iniciando API Gateway en el puerto 8090..."
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$base\api-gateway'; .\mvnw.cmd spring-boot:run"

Write-Host "Se abrieron tres terminales. Espere a que cada aplicación termine de iniciar."
