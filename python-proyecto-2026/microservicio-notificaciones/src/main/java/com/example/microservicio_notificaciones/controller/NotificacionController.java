package com.example.microservicio_notificaciones.controller;

import com.example.microservicio_notificaciones.assemblers.NotificacionAssembler;
import com.example.microservicio_notificaciones.model.Notificacion;
import com.example.microservicio_notificaciones.service.NotificacionService;
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
@RequestMapping("/api/notificaciones")
@Tag(name = "Controlador de Notificaciones", description = "Operaciones relacionadas con gestión de notificaciones a usuarios")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final NotificacionAssembler notificacionAssembler;

    public NotificacionController(NotificacionService notificacionService, NotificacionAssembler notificacionAssembler) {
        this.notificacionService = notificacionService;
        this.notificacionAssembler = notificacionAssembler;
    }

    @Operation(summary = "Listar notificaciones", description = "Obtiene todos los registros de notificaciones e incluye enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros encontrados correctamente"),
            @ApiResponse(responseCode = "204", description = "No existen registros", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Notificacion>>> listarNotificaciones() {
        List<Notificacion> lista = notificacionService.listarNotificaciones();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Notificacion>> modelos = lista.stream()
                .map(notificacionAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Notificacion>> respuesta = CollectionModel.of(
                modelos,
                linkTo(methodOn(NotificacionController.class).listarNotificaciones()).withSelfRel()
        );

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Buscar notificacion por ID", description = "Obtiene un registro específico mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Notificacion>> obtenerNotificacionPorId(@PathVariable Long id) {
        return notificacionService.obtenerNotificacionPorId(id)
                .map(notificacionAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear notificacion", description = "Registra un nuevo recurso de notificaciones.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Notificacion>> crearNotificacion(@Valid @RequestBody Notificacion notificacion) {
        Notificacion nuevo = notificacionService.guardarNotificacion(notificacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(notificacionAssembler.toModel(nuevo));
    }

    @Operation(summary = "Actualizar notificacion", description = "Actualiza un registro existente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Notificacion>> actualizarNotificacion(@PathVariable Long id, @Valid @RequestBody Notificacion notificacion) {
        return notificacionService.actualizarNotificacion(id, notificacion)
                .map(notificacionAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar notificacion", description = "Elimina un registro existente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNotificacion(@PathVariable Long id) {
        boolean eliminado = notificacionService.eliminarNotificacion(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
