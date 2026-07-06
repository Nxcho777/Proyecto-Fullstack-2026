package com.example.microservicio_usuarios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
@Schema(description = "Entidad que representa a un usuarioregistrado en nuestro sistema")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador unico de usuario",
            example = "1"
    )
    private Integer id;

    @NotBlank(message = "El username es de caracter obligatorio")
     @Schema(
            description = "Nombre de usuario utilizado para identificar al usuario en el sistema",
            example = "profeRobert"
    )
    private String username;

    @NotBlank(message = "La contraseña es de caracter obligatorio")
     @Schema(
            description = "Contraseña del usuario",
            example = "123456"
    )
    private String password;

    @Email(message = "El email debe tener un formato válido")
    @NotBlank(message = "El email es de caracter obligatorio")
       @Schema(
            description = "Correo electronico del usuario",
            example = "profeRobert@duocuc.com"
    )
    private String email;

    @NotBlank(message = "El rol es de caracter obligatorio")
     @Schema(
            description = "Rol asignado al usuario dentro de nuestro sistema",
            example = "Profesor"
    )
    private String rol;
}