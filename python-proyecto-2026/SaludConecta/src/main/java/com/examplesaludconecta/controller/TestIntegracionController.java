package com.examplesaludconecta.controller;

import com.examplesaludconecta.service.AutenticacionClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-conexion")
public class TestIntegracionController {

    @Autowired
    private AutenticacionClienteService authService;

    @GetMapping("/verificar/{email}")
    public String probarMicroservicio(@PathVariable String email) {
        boolean existeEnElOtroLado = authService.validarUsuarioEnMicroservicio(email);
        
        if (existeEnElOtroLado) {
            return "¡Email encontrado! SaludConecta ha viajado por la red al Microservicio y confirmó que el email '" + email + "' SÍ existe en SQLite.";
        } else {
            return "¡Upss algo ha salido mal! El microservicio nos dice que el email '" + email + "' no existe o el sistema está apagado.";
        }
    }
}