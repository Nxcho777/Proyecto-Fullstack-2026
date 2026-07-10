package com.example.microservicio_diagnosticos.model;

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
@Table(name = "diagnosticos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa gestión de diagnósticos médicos")
public class Diagnostico {

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

    @NotBlank(message = "La descripción es obligatoria")
    @Schema(description = "Descripción del diagnóstico")
    private String descripcion;

    @NotBlank(message = "La gravedad es obligatoria")
    @Schema(description = "Gravedad del diagnóstico")
    private String gravedad;

    @NotNull(message = "La fecha es obligatoria")
    @Schema(description = "Fecha del diagnóstico")
    private String fecha;
}
