package com.example.microservicio_usuarios.controller;

import com.example.microservicio_usuarios.dto.LoginRequest;
import com.example.microservicio_usuarios.model.Usuario;
import com.example.microservicio_usuarios.service.JwtService;
import com.example.microservicio_usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Autenticación",
        description = "Operaciones relacionadas con el inicio de sesión y generación de token JWT"
)
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    @Operation(
            summary = "Iniciar sesión",
            description = "Valida las credenciales del usuario mediante email y contraseña. Si son correctas, genera y retorna un token JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso, retorna token JWT"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud", content = @Content),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas o usuario no registrado", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        Usuario usuario = usuarioService.autenticarUsuario(email, password);
        String token = jwtService.generarToken(usuario.getEmail());

        return ResponseEntity.ok(
                Map.of(
                        "mensaje", "El usuario ha iniciado sesión exitosamente",
                        "token", token
                )
        );
    }
}
