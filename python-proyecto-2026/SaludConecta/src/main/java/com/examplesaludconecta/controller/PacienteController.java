package com.examplesaludconecta.controller;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examplesaludconecta.model.Paciente;
import com.examplesaludconecta.service.PacienteService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@Tag(name = "Controlador de Pacientes", description = "Operaciones relacionadas con gestión de pacientes")
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Operation(summary = "Listar pacientes", description = "Obtiene todos los registros de pacientes con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros encontrados correctamente")
    })
    @GetMapping
    public CollectionModel<EntityModel<Paciente>> listarPacientes() {
        List<EntityModel<Paciente>> pacientes = pacienteService.obtenerTodosLosPacientes()
                .stream()
                .map(this::agregarLinksPaciente)
                .collect(Collectors.toList());

        return CollectionModel.of(
                pacientes,
                linkTo(methodOn(PacienteController.class).listarPacientes()).withSelfRel()
        );
    }

    @Operation(summary = "Buscar por ID", description = "Obtiene un registro de pacientes mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Paciente>> obtenerPacientePorId(@PathVariable Long id) {
        return pacienteService.obtenerPorId(id)
                .map(paciente -> ResponseEntity.ok(agregarLinksPaciente(paciente)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear registro", description = "Crea un nuevo registro de pacientes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Paciente>> crearPaciente(@Valid @RequestBody Paciente paciente) {
        Paciente nuevoPaciente = pacienteService.guardarPaciente(paciente);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(agregarLinksPaciente(nuevoPaciente));
    }

    @Operation(summary = "Actualizar registro", description = "Actualiza un registro de pacientes mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Paciente>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Paciente paciente
    ) {
        Paciente actualizado = pacienteService.actualizarPaciente(id, paciente);

        return ResponseEntity.ok(agregarLinksPaciente(actualizado));
    }

    @Operation(summary = "Eliminar registro", description = "Elimina un registro de pacientes mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pacienteService.eliminarPaciente(id);

        return ResponseEntity.noContent().build();
    }

    private EntityModel<Paciente> agregarLinksPaciente(Paciente paciente) {
        return EntityModel.of(
                paciente,
                linkTo(methodOn(PacienteController.class)
                        .obtenerPacientePorId(paciente.getId())).withSelfRel(),

                linkTo(methodOn(PacienteController.class)
                        .listarPacientes()).withRel("pacientes"),

                linkTo(methodOn(PacienteController.class)
                        .actualizar(paciente.getId(), paciente)).withRel("actualizar"),

                linkTo(methodOn(PacienteController.class)
                        .eliminar(paciente.getId())).withRel("eliminar")
        );
    }
}