package com.example.microservicio_examenes.controller;

import com.example.microservicio_examenes.assemblers.ExamenAssembler;
import com.example.microservicio_examenes.model.Examen;
import com.example.microservicio_examenes.service.ExamenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/examenes")
@Tag(name = "Controlador de Examenes", description = "Operaciones relacionadas con gestión de exámenes médicos")
public class ExamenController {

    private final ExamenService examenService;
    private final ExamenAssembler examenAssembler;

    public ExamenController(ExamenService examenService, ExamenAssembler examenAssembler) {
        this.examenService = examenService;
        this.examenAssembler = examenAssembler;
    }

    @Operation(summary = "Listar examenes", description = "Obtiene todos los registros de examenes e incluye enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros encontrados correctamente"),
            @ApiResponse(responseCode = "204", description = "No existen registros", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Examen>>> listarExamenes() {
        List<Examen> lista = examenService.listarExamenes();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Examen>> modelos = lista.stream()
                .map(examenAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Examen>> respuesta = CollectionModel.of(
                modelos,
                linkTo(methodOn(ExamenController.class).listarExamenes()).withSelfRel()
        );

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Buscar examen por ID", description = "Obtiene un registro específico mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Examen>> obtenerExamenPorId(@PathVariable Long id) {
        return examenService.obtenerExamenPorId(id)
                .map(examenAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear examen", description = "Registra un nuevo recurso de examenes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Examen>> crearExamen(@Valid @RequestBody Examen examen) {
        Examen nuevo = examenService.guardarExamen(examen);
        return ResponseEntity.status(HttpStatus.CREATED).body(examenAssembler.toModel(nuevo));
    }

    @Operation(summary = "Actualizar examen", description = "Actualiza un registro existente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Examen>> actualizarExamen(@PathVariable Long id, @Valid @RequestBody Examen examen) {
        return examenService.actualizarExamen(id, examen)
                .map(examenAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar examen", description = "Elimina un registro existente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarExamen(@PathVariable Long id) {
        boolean eliminado = examenService.eliminarExamen(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
