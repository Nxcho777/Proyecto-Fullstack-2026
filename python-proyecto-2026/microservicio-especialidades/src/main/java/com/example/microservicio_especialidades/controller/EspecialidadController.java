package com.example.microservicio_especialidades.controller;

import com.example.microservicio_especialidades.assemblers.EspecialidadAssembler;
import com.example.microservicio_especialidades.model.Especialidad;
import com.example.microservicio_especialidades.service.EspecialidadService;
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
@RequestMapping("/api/especialidades")
@Tag(name = "Controlador de Especialidades", description = "Operaciones relacionadas con gestión de especialidades médicas")
public class EspecialidadController {

    private final EspecialidadService especialidadService;
    private final EspecialidadAssembler especialidadAssembler;

    public EspecialidadController(EspecialidadService especialidadService, EspecialidadAssembler especialidadAssembler) {
        this.especialidadService = especialidadService;
        this.especialidadAssembler = especialidadAssembler;
    }

    @Operation(summary = "Listar especialidades", description = "Obtiene todos los registros de especialidades e incluye enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros encontrados correctamente"),
            @ApiResponse(responseCode = "204", description = "No existen registros", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Especialidad>>> listarEspecialidades() {
        List<Especialidad> lista = especialidadService.listarEspecialidades();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Especialidad>> modelos = lista.stream()
                .map(especialidadAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Especialidad>> respuesta = CollectionModel.of(
                modelos,
                linkTo(methodOn(EspecialidadController.class).listarEspecialidades()).withSelfRel()
        );

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Buscar especialidad por ID", description = "Obtiene un registro específico mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Especialidad>> obtenerEspecialidadPorId(@PathVariable Long id) {
        return especialidadService.obtenerEspecialidadPorId(id)
                .map(especialidadAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear especialidad", description = "Registra un nuevo recurso de especialidades.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Especialidad>> crearEspecialidad(@Valid @RequestBody Especialidad especialidad) {
        Especialidad nuevo = especialidadService.guardarEspecialidad(especialidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(especialidadAssembler.toModel(nuevo));
    }

    @Operation(summary = "Actualizar especialidad", description = "Actualiza un registro existente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Especialidad>> actualizarEspecialidad(@PathVariable Long id, @Valid @RequestBody Especialidad especialidad) {
        return especialidadService.actualizarEspecialidad(id, especialidad)
                .map(especialidadAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar especialidad", description = "Elimina un registro existente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEspecialidad(@PathVariable Long id) {
        boolean eliminado = especialidadService.eliminarEspecialidad(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
