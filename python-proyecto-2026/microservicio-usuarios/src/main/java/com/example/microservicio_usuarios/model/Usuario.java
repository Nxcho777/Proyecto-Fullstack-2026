package com.example.microservicio_usuarios.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El username es de caracter obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es de caracter obligatorio")
    private String password;

    @Email(message = "El email debe tener un formato válido")
    @NotBlank(message = "El email es de caracter obligatorio")
    private String email;

    @NotBlank(message = "El rol es de caracter obligatorio")
    private String rol;
}