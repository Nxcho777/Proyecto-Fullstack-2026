package com.example.microservicio_pagos.controller;

import com.example.microservicio_pagos.assemblers.PagoAssembler;
import com.example.microservicio_pagos.model.Pago;
import com.example.microservicio_pagos.service.PagoService;
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
@RequestMapping("/api/pagos")
@Tag(name = "Controlador de Pagos", description = "Operaciones relacionadas con gestión de pagos de atenciones médicas")
public class PagoController {

    private final PagoService pagoService;
    private final PagoAssembler pagoAssembler;

    public PagoController(PagoService pagoService, PagoAssembler pagoAssembler) {
        this.pagoService = pagoService;
        this.pagoAssembler = pagoAssembler;
    }

    @Operation(summary = "Listar pagos", description = "Obtiene todos los registros de pagos e incluye enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros encontrados correctamente"),
            @ApiResponse(responseCode = "204", description = "No existen registros", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Pago>>> listarPagos() {
        List<Pago> lista = pagoService.listarPagos();

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Pago>> modelos = lista.stream()
                .map(pagoAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Pago>> respuesta = CollectionModel.of(
                modelos,
                linkTo(methodOn(PagoController.class).listarPagos()).withSelfRel()
        );

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Buscar pago por ID", description = "Obtiene un registro específico mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Pago>> obtenerPagoPorId(@PathVariable Long id) {
        return pagoService.obtenerPagoPorId(id)
                .map(pagoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear pago", description = "Registra un nuevo recurso de pagos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Pago>> crearPago(@Valid @RequestBody Pago pago) {
        Pago nuevo = pagoService.guardarPago(pago);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoAssembler.toModel(nuevo));
    }

    @Operation(summary = "Actualizar pago", description = "Actualiza un registro existente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Pago>> actualizarPago(@PathVariable Long id, @Valid @RequestBody Pago pago) {
        return pagoService.actualizarPago(id, pago)
                .map(pagoAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar pago", description = "Elimina un registro existente mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPago(@PathVariable Long id) {
        boolean eliminado = pagoService.eliminarPago(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
