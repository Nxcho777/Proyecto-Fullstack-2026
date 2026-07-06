package com.examplesaludconecta.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.examplesaludconecta.model.Tratamiento;
import com.examplesaludconecta.service.TratamientoService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/tratamientos")
public class TratamientoController {

    @Autowired
    private TratamientoService tratamientoService;

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

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Tratamiento>> buscarPorId(@PathVariable Long id) {
        return tratamientoService.obtenerPorId(id)
                .map(tratamiento -> ResponseEntity.ok(agregarLinksTratamiento(tratamiento)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<Tratamiento>> guardarTratamiento(
            @Valid @RequestBody Tratamiento tratamiento
    ) {
        Tratamiento nuevo = tratamientoService.guardarTratamiento(tratamiento);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(agregarLinksTratamiento(nuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Tratamiento>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Tratamiento tratamiento
    ) {
        Tratamiento actualizado = tratamientoService.actualizarTratamiento(id, tratamiento);

        return ResponseEntity.ok(agregarLinksTratamiento(actualizado));
    }

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