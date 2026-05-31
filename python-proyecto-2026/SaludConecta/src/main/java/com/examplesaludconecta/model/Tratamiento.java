package com.examplesaludconecta.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tratamientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INTEGER")
    private Long id;

    @NotBlank(message = "El diagnóstico es obligatorio")
    private String diagnostico;

    @NotBlank(message = "El medicamento es obligatorio")
    private String medicamento;

    @NotBlank(message = "La dosis es obligatoria")
    private String dosis;

    @Min(value = 1, message = "La duración del tratamiento debe ser mayor a 0")
    private int duracionDias;
}