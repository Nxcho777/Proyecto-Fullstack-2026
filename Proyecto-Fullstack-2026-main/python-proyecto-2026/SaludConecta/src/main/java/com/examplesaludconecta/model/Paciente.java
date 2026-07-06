package com.examplesaludconecta.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
@Table(name = "pacientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INTEGER")
    private Long id;

    @NotBlank(message = "El nombre debe ser obligatorio")
private String nombre;

    @NotBlank(message = "El apellido debe ser obligatorio")
private String apellido;

    @NotBlank(message = "El rut debe ser obligatorio")
private String rut;

    @Email(message = "El correo debe tener un formato válido para ingresarlo")
private String correo;

    @Pattern(regexp = "^[0-9+\\s-]{8,15}$", message = "El numero de teléfono no es válido")
private String telefono;

}