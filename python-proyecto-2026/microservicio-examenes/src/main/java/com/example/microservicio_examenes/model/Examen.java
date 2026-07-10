package com.example.microservicio_examenes.model;

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
@Table(name = "examenes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa gestión de exámenes médicos")
public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del registro")
    private Long id;

    @NotNull(message = "El paciente es obligatorio")
    @Schema(description = "Identificador del paciente")
    private Long pacienteId;

    @NotBlank(message = "El tipo de examen es obligatorio")
    @Schema(description = "Tipo de examen solicitado")
    private String tipoExamen;

    @NotBlank(message = "El resultado es obligatorio")
    @Schema(description = "Resultado del examen")
    private String resultado;

    @NotBlank(message = "El estado es obligatorio")
    @Schema(description = "Estado del examen")
    private String estado;

    @NotNull(message = "La fecha de solicitud es obligatoria")
    @Schema(description = "Fecha de solicitud")
    private String fechaSolicitud;
}
