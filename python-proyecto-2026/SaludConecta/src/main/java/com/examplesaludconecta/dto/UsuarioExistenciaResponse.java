package com.examplesaludconecta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta obtenida desde el microservicio de usuarios")
public class UsuarioExistenciaResponse {

    @Schema(description = "Correo consultado", example = "admin@saludconecta.cl")
    private String email;

    @Schema(description = "Indica si el usuario se encuentra registrado", example = "true")
    private boolean existe;

    @Schema(description = "Detalle de la respuesta remota", example = "El usuario se encuentra registrado")
    private String mensaje;
}
