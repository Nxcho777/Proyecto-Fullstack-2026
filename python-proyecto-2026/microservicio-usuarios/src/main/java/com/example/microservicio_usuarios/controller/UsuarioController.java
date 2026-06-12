package com.example.microservicio_usuarios.controller;

import com.example.microservicio_usuarios.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@Tag(
    name = "Controlador de Usuarios",
    description = "Operaciones relacionadas con la gestión y validación de usuarios"
)
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Operation(
        summary = "Verificar si el usuario existe",
        description = "Comprueba si un usuario se encuentra registrado en el sistema mediante su email."
    )
    @GetMapping("/existe/{email}")
    public ResponseEntity<Boolean> existeUsuario(@PathVariable String email) {

        boolean existe = usuarioRepository
                .findByEmail(email)
                .isPresent();

        return ResponseEntity.ok(existe);
    }
}