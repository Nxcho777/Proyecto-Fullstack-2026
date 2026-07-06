package com.examplesaludconecta.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.examplesaludconecta.model.Medico;
import com.examplesaludconecta.service.MedicoService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @GetMapping
    public CollectionModel<EntityModel<Medico>> listarMedicos() {
        List<EntityModel<Medico>> medicos = medicoService.obtenerTodosLosMedicos()
                .stream()
                .map(this::agregarLinksMedico)
                .toList();

        return CollectionModel.of(
                medicos,
                linkTo(methodOn(MedicoController.class).listarMedicos()).withSelfRel()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Medico>> buscarPorId(@PathVariable Long id) {
        return medicoService.obtenerPorId(id)
                .map(medico -> ResponseEntity.ok(agregarLinksMedico(medico)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<Medico>> guardarMedico(@Valid @RequestBody Medico medico) {
        Medico nuevo = medicoService.guardarMedico(medico);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(agregarLinksMedico(nuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Medico>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Medico medico
    ) {
        Medico actualizado = medicoService.actualizarMedico(id, medico);

        return ResponseEntity.ok(agregarLinksMedico(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        medicoService.eliminarMedico(id);

        return ResponseEntity.noContent().build();
    }

    private EntityModel<Medico> agregarLinksMedico(Medico medico) {
        return EntityModel.of(
                medico,
                linkTo(methodOn(MedicoController.class)
                        .buscarPorId(medico.getId())).withSelfRel(),

                linkTo(methodOn(MedicoController.class)
                        .listarMedicos()).withRel("medicos"),

                linkTo(methodOn(MedicoController.class)
                        .actualizar(medico.getId(), medico)).withRel("actualizar"),

                linkTo(methodOn(MedicoController.class)
                        .eliminar(medico.getId())).withRel("eliminar")
        );
    }
}