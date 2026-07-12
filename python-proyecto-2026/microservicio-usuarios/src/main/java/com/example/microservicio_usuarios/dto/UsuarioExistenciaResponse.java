package com.example.microservicio_usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta utilizada para comunicar la existencia de un usuario")
public class UsuarioExistenciaResponse {

    @Schema(description = "Correo consultado", example = "admin@saludconecta.cl")
    private String email;

    @Schema(description = "Indica si el usuario se encuentra registrado", example = "true")
    private boolean existe;

    @Schema(description = "Detalle del resultado", example = "El usuario se encuentra registrado")
    private String mensaje;
}
