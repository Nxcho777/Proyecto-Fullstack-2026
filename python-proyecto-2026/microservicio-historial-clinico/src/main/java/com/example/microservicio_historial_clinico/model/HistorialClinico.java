package com.example.microservicio_historial_clinico.model;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "historiales_clinicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa gestión del historial clínico de pacientes")
public class HistorialClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del registro")
    private Long id;

    @NotNull(message = "El paciente es obligatorio")
    @Schema(description = "Identificador del paciente")
    private Long pacienteId;

    @NotNull(message = "La fecha de registro es obligatoria")
    @Schema(description = "Fecha del registro clínico")
    private String fechaRegistro;

    @NotBlank(message = "Los antecedentes son obligatorios")
    @Schema(description = "Antecedentes médicos")
    private String antecedentes;

    @NotBlank(message = "Las observaciones son obligatorias")
    @Schema(description = "Observaciones clínicas")
    private String observaciones;

    @NotBlank(message = "Las alergias son obligatorias")
    @Schema(description = "Alergias registradas")
    private String alergias;
}
