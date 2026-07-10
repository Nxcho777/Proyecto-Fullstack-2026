package com.example.microservicio_citas.model;

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
@Table(name = "citas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa gestión de citas médicas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del registro")
    private Long id;

    @NotNull(message = "El paciente es obligatorio")
    @Schema(description = "Identificador del paciente")
    private Long pacienteId;

    @NotNull(message = "El médico es obligatorio")
    @Schema(description = "Identificador del médico")
    private Long medicoId;

    @NotNull(message = "La fecha es obligatoria")
    @Schema(description = "Fecha de la cita")
    private String fecha;

    @NotBlank(message = "La hora es obligatoria")
    @Schema(description = "Hora de la cita")
    private String hora;

    @NotBlank(message = "El motivo es obligatorio")
    @Schema(description = "Motivo de la cita")
    private String motivo;

    @NotBlank(message = "El estado es obligatorio")
    @Schema(description = "Estado de la cita")
    private String estado;
}
