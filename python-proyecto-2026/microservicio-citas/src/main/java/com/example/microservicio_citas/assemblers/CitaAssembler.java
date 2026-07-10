package com.example.microservicio_citas.assemblers;

import com.example.microservicio_citas.controller.CitaController;
import com.example.microservicio_citas.model.Cita;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CitaAssembler implements RepresentationModelAssembler<Cita, EntityModel<Cita>> {

    @Override
    public EntityModel<Cita> toModel(Cita cita) {
        return EntityModel.of(
                cita,
                linkTo(methodOn(CitaController.class).obtenerCitaPorId(cita.getId())).withSelfRel(),
                linkTo(methodOn(CitaController.class).listarCitas()).withRel("citas"),
                linkTo(methodOn(CitaController.class).actualizarCita(cita.getId(), cita)).withRel("actualizar"),
                linkTo(methodOn(CitaController.class).eliminarCita(cita.getId())).withRel("eliminar")
        );
    }
}
