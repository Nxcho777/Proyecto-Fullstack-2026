package com.example.microservicio_usuarios.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    @Email(message = "Debe ingresar un email válido")
    @NotBlank(message = "El correo electrónico es de caracter obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es de caracter obligatorio")
    private String password;
}