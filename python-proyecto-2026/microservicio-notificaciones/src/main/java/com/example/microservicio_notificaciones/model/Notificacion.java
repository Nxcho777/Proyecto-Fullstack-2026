package com.example.microservicio_notificaciones.model;

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
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa gestión de notificaciones a usuarios")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del registro")
    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    @Schema(description = "Identificador del usuario")
    private Long usuarioId;

    @NotBlank(message = "El mensaje es obligatorio")
    @Schema(description = "Mensaje de la notificación")
    private String mensaje;

    @NotBlank(message = "El canal es obligatorio")
    @Schema(description = "Canal de envío")
    private String canal;

    @NotBlank(message = "El estado es obligatorio")
    @Schema(description = "Estado de la notificación")
    private String estado;

    @NotNull(message = "La fecha de envío es obligatoria")
    @Schema(description = "Fecha de envío")
    private String fechaEnvio;
}
