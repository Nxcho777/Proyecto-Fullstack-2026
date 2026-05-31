package com.example.microservicio_usuarios.controller;

import com.example.microservicio_usuarios.dto.LoginRequest;
import jakarta.validation.Valid;
import com.example.microservicio_usuarios.model.Usuario;
import com.example.microservicio_usuarios.repository.UsuarioRepository;
import com.example.microservicio_usuarios.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
    String username = loginRequest.getUsername();
    String password = loginRequest.getPassword();
    Usuario usuario = usuarioRepository.findByUsername(username)
        .orElse(null);

    if (usuario == null) {
        return ResponseEntity.status(401)
                .body(Map.of("Error", "El usuario ingresado no existe"));
    }

    if (!usuario.getPassword().equals(password)) {
        return ResponseEntity.status(401)
                .body(Map.of("Error", "La contraseña ingresada es incorrecta"));
    }

    String token = jwtService.generarToken(username);
        return ResponseEntity.ok(
            Map.of(
                    "mensaje", "El usuario ha iniciado sesión exitosamente",
                    "token", token
            )
        );
    }
}