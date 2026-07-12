package com.examplesaludconecta.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tratamientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un tratamiento médico")
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INTEGER")
    @Schema(description = "Identificador único del tratamiento", example = "1")
    private Long id;

    @NotBlank(message = "El diagnóstico es obligatorio")
    @Size(max = 255, message = "El diagnóstico no puede superar los 255 caracteres")
    @Column(nullable = false)
    @Schema(description = "Diagnóstico asociado al tratamiento", example = "Hipertensión")
    private String diagnostico;

    @NotBlank(message = "El medicamento es obligatorio")
    @Size(max = 255, message = "El medicamento no puede superar los 255 caracteres")
    @Column(nullable = false)
    @Schema(description = "Medicamento indicado", example = "Losartán")
    private String medicamento;

    @NotBlank(message = "La dosis es obligatoria")
    @Size(max = 255, message = "La dosis no puede superar los 255 caracteres")
    @Column(nullable = false)
    @Schema(description = "Dosis indicada", example = "50 mg cada 12 horas")
    private String dosis;

    @Min(value = 1, message = "La duración del tratamiento debe ser mayor a 0")
    @Max(value = 365, message = "La duración del tratamiento no puede superar los 365 días")
    @Column(name = "duracion_dias", nullable = false)
    @Schema(description = "Duración del tratamiento expresada en días", example = "30")
    private int duracionDias;
}
