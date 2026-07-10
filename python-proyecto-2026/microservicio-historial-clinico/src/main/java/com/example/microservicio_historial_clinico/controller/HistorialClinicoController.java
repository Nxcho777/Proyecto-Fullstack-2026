package com.example.microservicio_historial_clinico.controller;

import com.example.microservicio_historial_clinico.assemblers.HistorialClinicoAssembler;
import com.example.microservicio_historial_clinico.model.HistorialClinico;
import com.example.microservicio_historial_clinico.service.HistorialClinicoService;
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
@RequestMapping("/api/historiales-clinicos")
@Tag(name = "Controlador de HistorialesClinicos", description = "Operaciones relacionadas con gestión del historial clínico de pacientes")
public class HistorialClinicoController {

    private final HistorialClinicoService historialClinicoService;
    private final HistorialClinicoAssembler historialClinicoAssembler;

    public HistorialClinicoController(HistorialClinicoService historialClinicoService, HistorialClinicoAssembler historialClinicoAssembler) {
        this.historialClinicoService = historialClinicoService;
        this.historialClinicoAssembler = historialClinicoAssembler;
    }

    @Operation(summary = "Listar historiales-clinicos", description = "Obtiene todos los registros de historiales-clinicos e incluye enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros encontrados correctamente"),
            @ApiResponse(responseCode = "204", description = "No existen registros", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<HistorialClinico>>> listarHistorialesClinicos() {
        List<HistorialClinico> lista = historialClinicoService.listarHistorialesClinicos();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<HistorialClinico>> modelos = lista.stream()
                .map(historialClinicoAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<HistorialClinico>> respuesta = CollectionModel.of(
                modelos,
                linkTo(methodOn(HistorialClinicoController.class).listarHistorialesClinicos()).withSelfRel()
        );

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Buscar historialClinico por ID", description = "Obtiene un registro específico mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<HistorialClinico>> obtenerHistorialClinicoPorId(@PathVariable Long id) {
        return historialClinicoService.obtenerHistorialClinicoPorId(id)
                .map(historialClinicoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear historialClinico", description = "Registra un nuevo recurso de historiales-clinicos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<HistorialClinico>> crearHistorialClinico(@Valid @RequestBody HistorialClinico historialClinico) {
        HistorialClinico nuevo = historialClinicoService.guardarHistorialClinico(historialClinico);
        return ResponseEntity.status(HttpStatus.CREATED).body(historialClinicoAssembler.toModel(nuevo));
    }

    @Operation(summary = "Actualizar historialClinico", description = "Actualiza un registro existente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<HistorialClinico>> actualizarHistorialClinico(@PathVariable Long id, @Valid @RequestBody HistorialClinico historialClinico) {
        return historialClinicoService.actualizarHistorialClinico(id, historialClinico)
                .map(historialClinicoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar historialClinico", description = "Elimina un registro existente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHistorialClinico(@PathVariable Long id) {
        boolean eliminado = historialClinicoService.eliminarHistorialClinico(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
