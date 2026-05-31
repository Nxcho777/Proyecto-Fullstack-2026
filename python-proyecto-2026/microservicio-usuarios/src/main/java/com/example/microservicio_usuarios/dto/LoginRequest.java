package com.example.microservicio_usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "El username es de caracter obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es de caracter obligatorio")
    private String password;
}