package com.example.microservicio_citas.controller;

import com.example.microservicio_citas.assemblers.CitaAssembler;
import com.example.microservicio_citas.model.Cita;
import com.example.microservicio_citas.service.CitaService;
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
@RequestMapping("/api/citas")
@Tag(name = "Controlador de Citas", description = "Operaciones relacionadas con gestión de citas médicas")
public class CitaController {

    private final CitaService citaService;
    private final CitaAssembler citaAssembler;

    public CitaController(CitaService citaService, CitaAssembler citaAssembler) {
        this.citaService = citaService;
        this.citaAssembler = citaAssembler;
    }

    @Operation(summary = "Listar citas", description = "Obtiene todos los registros de citas e incluye enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros encontrados correctamente"),
            @ApiResponse(responseCode = "204", description = "No existen registros", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Cita>>> listarCitas() {
        List<Cita> lista = citaService.listarCitas();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Cita>> modelos = lista.stream()
                .map(citaAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Cita>> respuesta = CollectionModel.of(
                modelos,
                linkTo(methodOn(CitaController.class).listarCitas()).withSelfRel()
        );

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Buscar cita por ID", description = "Obtiene un registro específico mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Cita>> obtenerCitaPorId(@PathVariable Long id) {
        return citaService.obtenerCitaPorId(id)
                .map(citaAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear cita", description = "Registra un nuevo recurso de citas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Cita>> crearCita(@Valid @RequestBody Cita cita) {
        Cita nuevo = citaService.guardarCita(cita);
        return ResponseEntity.status(HttpStatus.CREATED).body(citaAssembler.toModel(nuevo));
    }

    @Operation(summary = "Actualizar cita", description = "Actualiza un registro existente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Cita>> actualizarCita(@PathVariable Long id, @Valid @RequestBody Cita cita) {
        return citaService.actualizarCita(id, cita)
                .map(citaAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar cita", description = "Elimina un registro existente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCita(@PathVariable Long id) {
        boolean eliminado = citaService.eliminarCita(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
