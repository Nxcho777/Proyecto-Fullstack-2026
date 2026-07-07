package com.example.microservicio_usuarios.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.example.microservicio_usuarios.dto.LoginRequest;
import jakarta.validation.Valid;
import com.example.microservicio_usuarios.model.Usuario;
import com.example.microservicio_usuarios.service.UsuarioService;
import com.example.microservicio_usuarios.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    @ApiResponse(responseCode = "200", description = "El inicio de sesionfure exitoso, retorna token JWT"),
    @ApiResponse(responseCode = "400", description = "Datos invalidos en el envio de la solicitud", content = @Content),
    @ApiResponse(responseCode = "401", description = "Las credenciales son incorrectas o el usuario no esta registrado", content = @Content)
    }) 
 @PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

    String email = request.getEmail();
    String password = request.getPassword();

    Usuario usuario = usuarioService.buscarPorEmail(email)
            .orElse(null);

    if (usuario == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("Error", "El email ingresado no está registrado o no existe"));
    }

    if (!usuario.getPassword().equals(password)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("Error", "La contraseña ingresada es incorrecta"));
    }

    String token = jwtService.generarToken(usuario.getEmail());

    return ResponseEntity.ok(
            Map.of(
                    "mensaje", "El usuario ha iniciado sesión exitosamente",
                    "token", token
            )
    );
}
}