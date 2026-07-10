package com.example.microservicio_recetas.controller;

import com.example.microservicio_recetas.assemblers.RecetaAssembler;
import com.example.microservicio_recetas.model.Receta;
import com.example.microservicio_recetas.service.RecetaService;
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
@RequestMapping("/api/recetas")
@Tag(name = "Controlador de Recetas", description = "Operaciones relacionadas con gestión de recetas médicas")
public class RecetaController {

    private final RecetaService recetaService;
    private final RecetaAssembler recetaAssembler;

    public RecetaController(RecetaService recetaService, RecetaAssembler recetaAssembler) {
        this.recetaService = recetaService;
        this.recetaAssembler = recetaAssembler;
    }

    @Operation(summary = "Listar recetas", description = "Obtiene todos los registros de recetas e incluye enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros encontrados correctamente"),
            @ApiResponse(responseCode = "204", description = "No existen registros", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Receta>>> listarRecetas() {
        List<Receta> lista = recetaService.listarRecetas();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Receta>> modelos = lista.stream()
                .map(recetaAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Receta>> respuesta = CollectionModel.of(
                modelos,
                linkTo(methodOn(RecetaController.class).listarRecetas()).withSelfRel()
        );

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Buscar receta por ID", description = "Obtiene un registro específico mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Receta>> obtenerRecetaPorId(@PathVariable Long id) {
        return recetaService.obtenerRecetaPorId(id)
                .map(recetaAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear receta", description = "Registra un nuevo recurso de recetas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Receta>> crearReceta(@Valid @RequestBody Receta receta) {
        Receta nuevo = recetaService.guardarReceta(receta);
        return ResponseEntity.status(HttpStatus.CREATED).body(recetaAssembler.toModel(nuevo));
    }

    @Operation(summary = "Actualizar receta", description = "Actualiza un registro existente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Receta>> actualizarReceta(@PathVariable Long id, @Valid @RequestBody Receta receta) {
        return recetaService.actualizarReceta(id, receta)
                .map(recetaAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar receta", description = "Elimina un registro existente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReceta(@PathVariable Long id) {
        boolean eliminado = recetaService.eliminarReceta(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
