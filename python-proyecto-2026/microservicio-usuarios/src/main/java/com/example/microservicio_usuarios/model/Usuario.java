package com.example.microservicio_usuarios.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
@Schema(description = "Entidad que representa a un usuario registrado en nuestro sistema")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de usuario", example = "1")
    private Integer id;

    @NotBlank(message = "El username es de caracter obligatorio")
    @Size(min = 3, max = 50, message = "El username debe contener entre 3 y 50 caracteres")
    @Column(nullable = false, unique = true)
    @Schema(description = "Nombre de usuario utilizado para identificar al usuario", example = "profeRobert")
    private String username;

    @NotBlank(message = "La contraseña es de caracter obligatorio")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "Contraseña del usuario", example = "123456", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Email(message = "El email debe tener un formato válido")
    @NotBlank(message = "El email es de caracter obligatorio")
    @Column(nullable = false, unique = true)
    @Schema(description = "Correo electrónico del usuario", example = "profeRobert@duocuc.com")
    private String email;

    @NotBlank(message = "El rol es de caracter obligatorio")
    @Size(max = 30, message = "El rol no puede superar los 30 caracteres")
    @Column(nullable = false)
    @Schema(description = "Rol asignado al usuario dentro de nuestro sistema", example = "MEDICO")
    private String rol;
}
