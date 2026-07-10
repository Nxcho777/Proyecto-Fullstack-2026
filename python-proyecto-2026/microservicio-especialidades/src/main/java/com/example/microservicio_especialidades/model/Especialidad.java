package com.example.microservicio_especialidades.model;

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
@Table(name = "especialidades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa gestión de especialidades médicas")
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del registro")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre de la especialidad")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Schema(description = "Descripción de la especialidad")
    private String descripcion;

    @NotBlank(message = "El área es obligatoria")
    @Schema(description = "Área médica asociada")
    private String area;
    
    @NotNull(message = "El estado activo es obligatorio")
    @Schema(description = "Indica si la especialidad está activa")
    private Boolean activa;
}
