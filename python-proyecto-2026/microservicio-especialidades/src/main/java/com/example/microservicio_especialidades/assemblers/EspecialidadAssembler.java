package com.example.microservicio_especialidades.assemblers;

import com.example.microservicio_especialidades.controller.EspecialidadController;
import com.example.microservicio_especialidades.model.Especialidad;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EspecialidadAssembler implements RepresentationModelAssembler<Especialidad, EntityModel<Especialidad>> {

    @Override
    public EntityModel<Especialidad> toModel(Especialidad especialidad) {
        return EntityModel.of(
                especialidad,
                linkTo(methodOn(EspecialidadController.class).obtenerEspecialidadPorId(especialidad.getId())).withSelfRel(),
                linkTo(methodOn(EspecialidadController.class).listarEspecialidades()).withRel("especialidades"),
                linkTo(methodOn(EspecialidadController.class).actualizarEspecialidad(especialidad.getId(), especialidad)).withRel("actualizar"),
                linkTo(methodOn(EspecialidadController.class).eliminarEspecialidad(especialidad.getId())).withRel("eliminar")
        );
    }
}
