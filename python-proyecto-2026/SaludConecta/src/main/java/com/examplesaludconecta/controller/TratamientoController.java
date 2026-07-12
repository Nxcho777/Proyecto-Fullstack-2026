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

import com.examplesaludconecta.exception.ResourceNotFoundException;
import com.examplesaludconecta.model.Tratamiento;
import com.examplesaludconecta.service.TratamientoService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@Tag(name = "Controlador de Tratamientos", description = "Operaciones relacionadas con gestión de tratamientos")
@RequestMapping("/api/tratamientos")
public class TratamientoController {

    @Autowired
    private TratamientoService tratamientoService;

    @Operation(summary = "Listar tratamientos", description = "Obtiene todos los registros de tratamientos con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros encontrados correctamente")
    })
    @GetMapping
    public CollectionModel<EntityModel<Tratamiento>> listarTratamientos() {
        List<EntityModel<Tratamiento>> tratamientos = tratamientoService.obtenerTodosLosTratamientos()
                .stream()
                .map(this::agregarLinksTratamiento)
                .toList();

        return CollectionModel.of(
                tratamientos,
                linkTo(methodOn(TratamientoController.class).listarTratamientos()).withSelfRel()
        );
    }

    @Operation(summary = "Buscar por ID", description = "Obtiene un registro de tratamientos mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Tratamiento>> buscarPorId(@PathVariable Long id) {
        Tratamiento tratamiento = tratamientoService.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tratamiento no encontrado con id: " + id));

        return ResponseEntity.ok(agregarLinksTratamiento(tratamiento));
    }

    @Operation(summary = "Crear registro", description = "Crea un nuevo registro de tratamientos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Tratamiento>> guardarTratamiento(
            @Valid @RequestBody Tratamiento tratamiento
    ) {
        Tratamiento nuevo = tratamientoService.guardarTratamiento(tratamiento);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(agregarLinksTratamiento(nuevo));
    }

    @Operation(summary = "Actualizar registro", description = "Actualiza un registro de tratamientos mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Tratamiento>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Tratamiento tratamiento
    ) {
        Tratamiento actualizado = tratamientoService.actualizarTratamiento(id, tratamiento);

        return ResponseEntity.ok(agregarLinksTratamiento(actualizado));
    }

    @Operation(summary = "Eliminar registro", description = "Elimina un registro de tratamientos mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tratamientoService.eliminarTratamiento(id);

        return ResponseEntity.noContent().build();
    }

    private EntityModel<Tratamiento> agregarLinksTratamiento(Tratamiento tratamiento) {
        return EntityModel.of(
                tratamiento,
                linkTo(methodOn(TratamientoController.class)
                        .buscarPorId(tratamiento.getId())).withSelfRel(),

                linkTo(methodOn(TratamientoController.class)
                        .listarTratamientos()).withRel("tratamientos"),

                linkTo(methodOn(TratamientoController.class)
                        .actualizar(tratamiento.getId(), tratamiento)).withRel("actualizar"),

                linkTo(methodOn(TratamientoController.class)
                        .eliminar(tratamiento.getId())).withRel("eliminar")
        );
    }
}
