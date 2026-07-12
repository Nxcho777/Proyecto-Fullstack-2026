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
@Table(name = "pacientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa a un paciente registrado en SaludConecta")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INTEGER")
    @Schema(description = "Identificador único del paciente", example = "1")
    private Long id;

    @NotBlank(message = "El nombre debe ser obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(nullable = false)
    @Schema(description = "Nombre del paciente", example = "Carlos")
    private String nombre;

    @NotBlank(message = "El apellido debe ser obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    @Column(nullable = false)
    @Schema(description = "Apellido del paciente", example = "Soto")
    private String apellido;

    @NotBlank(message = "El rut debe ser obligatorio")
    @Pattern(regexp = "^\\d{1,2}\\.\\d{3}\\.\\d{3}[-][0-9Kk]$", message = "El RUT no es válido")
    @Column(nullable = false, unique = true)
    @Schema(description = "RUT del paciente", example = "13.456.789-K")
    private String rut;

    @Email(message = "El correo debe tener un formato válido para ingresarlo")
    @NotBlank(message = "El correo debe ser obligatorio")
    @Column(nullable = false, unique = true)
    @Schema(description = "Correo electrónico del paciente", example = "paciente@gmail.com")
    private String correo;

    @NotBlank(message = "El número de teléfono es obligatorio")
    @Pattern(regexp = "^[0-9+\\s-]{8,15}$", message = "El numero de teléfono no es válido")
    @Column(nullable = false)
    @Schema(description = "Número telefónico del paciente", example = "+56912345678")
    private String telefono;
}
