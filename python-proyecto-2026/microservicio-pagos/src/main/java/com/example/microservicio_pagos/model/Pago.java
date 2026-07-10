package com.example.microservicio_pagos.model;

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
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa gestión de pagos de atenciones médicas")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del registro")
    private Long id;

    @NotNull(message = "El paciente es obligatorio")
    @Schema(description = "Identificador del paciente")
    private Long pacienteId;

    @NotNull(message = "El monto es obligatorio")
    @Schema(description = "Monto del pago")
    private Double monto;

    @NotBlank(message = "El método de pago es obligatorio")
    @Schema(description = "Método de pago utilizado")
    private String metodoPago;

    @NotBlank(message = "El estado es obligatorio")
    @Schema(description = "Estado del pago")
    private String estado;

    @NotNull(message = "La fecha de pago es obligatoria")
    @Schema(description = "Fecha del pago")
    private String fechaPago;
}
