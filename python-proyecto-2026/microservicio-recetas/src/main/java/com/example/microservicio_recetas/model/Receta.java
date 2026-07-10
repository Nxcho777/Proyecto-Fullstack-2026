package com.example.microservicio_recetas.model;

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
@Table(name = "recetas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa gestión de recetas médicas")
public class Receta {

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

    @NotBlank(message = "El medicamento es obligatorio")
    @Schema(description = "Medicamento indicado")
    private String medicamento;

    @NotBlank(message = "La dosis es obligatoria")
    @Schema(description = "Dosis indicada")
    private String dosis;

    @NotBlank(message = "Las indicaciones son obligatorias")
    @Schema(description = "Indicaciones de uso")
    private String indicaciones;

    @NotNull(message = "La fecha de emisión es obligatoria")
    @Schema(description = "Fecha de emisión de la receta")
    private String fechaEmision;
}
