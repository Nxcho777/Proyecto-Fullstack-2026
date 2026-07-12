package com.example.microservicio_usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @Email(message = "Debe ingresar un email válido")
    @NotBlank(message = "El correo electrónico es de caracter obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es de caracter obligatorio")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
}
