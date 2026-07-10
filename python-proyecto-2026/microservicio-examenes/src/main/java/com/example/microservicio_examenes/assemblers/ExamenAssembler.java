package com.example.microservicio_examenes.assemblers;

import com.example.microservicio_examenes.controller.ExamenController;
import com.example.microservicio_examenes.model.Examen;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ExamenAssembler implements RepresentationModelAssembler<Examen, EntityModel<Examen>> {

    @Override
    public EntityModel<Examen> toModel(Examen examen) {
        return EntityModel.of(
                examen,
                linkTo(methodOn(ExamenController.class).obtenerExamenPorId(examen.getId())).withSelfRel(),
                linkTo(methodOn(ExamenController.class).listarExamenes()).withRel("examenes"),
                linkTo(methodOn(ExamenController.class).actualizarExamen(examen.getId(), examen)).withRel("actualizar"),
                linkTo(methodOn(ExamenController.class).eliminarExamen(examen.getId())).withRel("eliminar")
        );
    }
}
