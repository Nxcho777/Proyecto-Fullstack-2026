package com.example.microservicio_diagnosticos.controller;

import com.example.microservicio_diagnosticos.assemblers.DiagnosticoAssembler;
import com.example.microservicio_diagnosticos.model.Diagnostico;
import com.example.microservicio_diagnosticos.service.DiagnosticoService;
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
@RequestMapping("/api/diagnosticos")
@Tag(name = "Controlador de Diagnosticos", description = "Operaciones relacionadas con gestión de diagnósticos médicos")
public class DiagnosticoController {

    private final DiagnosticoService diagnosticoService;
    private final DiagnosticoAssembler diagnosticoAssembler;

    public DiagnosticoController(DiagnosticoService diagnosticoService, DiagnosticoAssembler diagnosticoAssembler) {
        this.diagnosticoService = diagnosticoService;
        this.diagnosticoAssembler = diagnosticoAssembler;
    }

    @Operation(summary = "Listar diagnosticos", description = "Obtiene todos los registros de diagnosticos e incluye enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros encontrados correctamente"),
            @ApiResponse(responseCode = "204", description = "No existen registros", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Diagnostico>>> listarDiagnosticos() {
        List<Diagnostico> lista = diagnosticoService.listarDiagnosticos();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Diagnostico>> modelos = lista.stream()
                .map(diagnosticoAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Diagnostico>> respuesta = CollectionModel.of(
                modelos,
                linkTo(methodOn(DiagnosticoController.class).listarDiagnosticos()).withSelfRel()
        );

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Buscar diagnostico por ID", description = "Obtiene un registro específico mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Diagnostico>> obtenerDiagnosticoPorId(@PathVariable Long id) {
        return diagnosticoService.obtenerDiagnosticoPorId(id)
                .map(diagnosticoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear diagnostico", description = "Registra un nuevo recurso de diagnosticos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Diagnostico>> crearDiagnostico(@Valid @RequestBody Diagnostico diagnostico) {
        Diagnostico nuevo = diagnosticoService.guardarDiagnostico(diagnostico);
        return ResponseEntity.status(HttpStatus.CREATED).body(diagnosticoAssembler.toModel(nuevo));
    }

    @Operation(summary = "Actualizar diagnostico", description = "Actualiza un registro existente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Diagnostico>> actualizarDiagnostico(@PathVariable Long id, @Valid @RequestBody Diagnostico diagnostico) {
        return diagnosticoService.actualizarDiagnostico(id, diagnostico)
                .map(diagnosticoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar diagnostico", description = "Elimina un registro existente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDiagnostico(@PathVariable Long id) {
        boolean eliminado = diagnosticoService.eliminarDiagnostico(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
