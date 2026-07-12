package com.examplesaludconecta.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API SaludConecta",
                version = "1.0",
                description = "API principal para la gestión de pacientes, médicos, tratamientos y comunicación con usuarios"
        )
)
public class OpenApiConfig {
}
