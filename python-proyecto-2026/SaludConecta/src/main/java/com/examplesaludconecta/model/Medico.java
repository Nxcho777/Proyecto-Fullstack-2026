package com.examplesaludconecta.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa a un médico registrado en SaludConecta")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INTEGER")
    @Schema(description = "Identificador único del médico", example = "1")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(nullable = false)
    @Schema(description = "Nombre del médico", example = "María")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    @Column(nullable = false)
    @Schema(description = "Apellido del médico", example = "González")
    private String apellido;

    @NotBlank(message = "El RUT es obligatorio")
    @Pattern(regexp = "^\\d{1,2}\\.\\d{3}\\.\\d{3}[-][0-9Kk]$", message = "El RUT no es válido")
    @Column(nullable = false, unique = true)
    @Schema(description = "RUT del médico", example = "12.345.678-9")
    private String rut;

    @NotBlank(message = "La especialidad es obligatoria")
    @Size(max = 100, message = "La especialidad no puede superar los 100 caracteres")
    @Column(nullable = false)
    @Schema(description = "Especialidad médica", example = "Cardiología")
    private String especialidad;

    @Email(message = "El correo electrónico no es válido")
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Column(nullable = false, unique = true)
    @Schema(description = "Correo electrónico del médico", example = "medico@saludconecta.cl")
    private String email;
}
