package com.example.microservicio_usuarios.controller;

import com.example.microservicio_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/existe/{username}")
    public boolean existeUsuario(@PathVariable String username) {
        return usuarioRepository.findByUsername(username).isPresent();
    }
}