package com.examplesaludconecta.controller;

import com.examplesaludconecta.dto.UsuarioExistenciaResponse;
import com.examplesaludconecta.service.AutenticacionClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/test-conexion")
@Tag(name = "Integración de Usuarios", description = "Comunicación REST entre SaludConecta y microservicio-usuarios")
public class TestIntegracionController {

    @Autowired
    private AutenticacionClienteService authService;

    @Operation(
            summary = "Verificar usuario en otro microservicio",
            description = "Consulta mediante WebClient si un correo está registrado en microservicio-usuarios"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta remota realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Correo inválido", content = @Content),
            @ApiResponse(responseCode = "503", description = "Microservicio de usuarios no disponible", content = @Content)
    })
    @GetMapping("/verificar/{email}")
    public ResponseEntity<UsuarioExistenciaResponse> probarMicroservicio(
            @PathVariable @Email(message = "El correo ingresado no tiene un formato válido") String email
    ) {
        UsuarioExistenciaResponse respuesta = authService.obtenerValidacionUsuario(email);
        return ResponseEntity.ok(respuesta);
    }
}
