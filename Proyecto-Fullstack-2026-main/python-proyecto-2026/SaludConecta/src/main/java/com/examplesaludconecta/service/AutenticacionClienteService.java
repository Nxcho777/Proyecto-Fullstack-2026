package com.examplesaludconecta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AutenticacionClienteService {

    @Autowired
    private WebClient webClient;

    public boolean validarUsuarioEnMicroservicio(String username) {
        try {
            Boolean existe = webClient.get()
                    .uri("/existe/{username}", username)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block(); // .block() espera la respuesta del servidor antes de continuar
            
            return existe != null && existe;
        } catch (Exception e) {
            System.err.println("Error al intentar conectar con el microservicio de usuarios: " + e.getMessage());
            return false;
        }
    }
}