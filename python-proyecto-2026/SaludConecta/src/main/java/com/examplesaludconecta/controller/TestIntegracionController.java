package com.examplesaludconecta.controller;

import com.examplesaludconecta.service.AutenticacionClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-conexion")
public class TestIntegracionController {

    @Autowired
    private AutenticacionClienteService authService;

    @GetMapping("/verificar/{username}")
    public String probarMicroservicio(@PathVariable String username) {
        boolean existeEnElOtroLado = authService.validarUsuarioEnMicroservicio(username);
        
        if (existeEnElOtroLado) {
            return "¡Usuario encontrado! SaludConecta ha viajado por la red al Microservicio y confirmó que el usuario '" + username + "' SÍ existe en SQLite.";
        } else {
            return "¡Upss algo ha salido mal! El microservicio nos dice que el usuario '" + username + "' no existe o el sistema está apagado.";
        }
    }
}