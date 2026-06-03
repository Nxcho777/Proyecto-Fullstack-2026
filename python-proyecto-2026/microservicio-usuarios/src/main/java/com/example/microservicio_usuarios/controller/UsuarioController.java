package com.example.microservicio_usuarios.controller;

import org.springframework.http.ResponseEntity;
import com.example.microservicio_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/existe/{email}")
    public ResponseEntity<Boolean> existeUsuario(@PathVariable String email) {

    boolean existe = usuarioRepository
            .findByEmail(email)
            .isPresent();

    return ResponseEntity.ok(existe);
    }
}