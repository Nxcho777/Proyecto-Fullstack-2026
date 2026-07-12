package com.example.microservicio_usuarios.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API Microservicio de Usuarios",
                version = "1.0",
                description = "API para gestión, validación y autenticación de usuarios de SaludConecta"
        )
)
public class OpenApiConfig {
}
