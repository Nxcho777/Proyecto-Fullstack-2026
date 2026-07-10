package com.examplesaludconecta.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.*;

import com.examplesaludconecta.model.Medico;
import com.examplesaludconecta.service.MedicoService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@Tag(name = "Controlador de Médicos", description = "Operaciones relacionadas con gestión de médicos")
@RequestMapping("/api/medicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @Operation(summary = "Listar medicos", description = "Obtiene todos los registros de medicos con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros encontrados correctamente")
    })
    @GetMapping
    public CollectionModel<EntityModel<Medico>> listarMedicos() {
        List<EntityModel<Medico>> medicos = medicoService.obtenerTodosLosMedicos()
                .stream()
                .map(this::agregarLinksMedico)
                .toList();

        return CollectionModel.of(
                medicos,
                linkTo(methodOn(MedicoController.class).listarMedicos()).withSelfRel()
        );
    }

    @Operation(summary = "Buscar por ID", description = "Obtiene un registro de medicos mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Medico>> buscarPorId(@PathVariable Long id) {
        return medicoService.obtenerPorId(id)
                .map(medico -> ResponseEntity.ok(agregarLinksMedico(medico)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear registro", description = "Crea un nuevo registro de medicos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Medico>> guardarMedico(@Valid @RequestBody Medico medico) {
        Medico nuevo = medicoService.guardarMedico(medico);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(agregarLinksMedico(nuevo));
    }

    @Operation(summary = "Actualizar registro", description = "Actualiza un registro de medicos mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Medico>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Medico medico
    ) {
        Medico actualizado = medicoService.actualizarMedico(id, medico);

        return ResponseEntity.ok(agregarLinksMedico(actualizado));
    }

    @Operation(summary = "Eliminar registro", description = "Elimina un registro de medicos mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        medicoService.eliminarMedico(id);

        return ResponseEntity.noContent().build();
    }

    private EntityModel<Medico> agregarLinksMedico(Medico medico) {
        return EntityModel.of(
                medico,
                linkTo(methodOn(MedicoController.class)
                        .buscarPorId(medico.getId())).withSelfRel(),

                linkTo(methodOn(MedicoController.class)
                        .listarMedicos()).withRel("medicos"),

                linkTo(methodOn(MedicoController.class)
                        .actualizar(medico.getId(), medico)).withRel("actualizar"),

                linkTo(methodOn(MedicoController.class)
                        .eliminar(medico.getId())).withRel("eliminar")
        );
    }
}