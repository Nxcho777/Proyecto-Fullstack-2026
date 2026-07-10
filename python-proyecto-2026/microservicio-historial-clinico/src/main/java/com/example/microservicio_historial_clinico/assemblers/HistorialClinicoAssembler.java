package com.example.microservicio_historial_clinico.assemblers;

import com.example.microservicio_historial_clinico.controller.HistorialClinicoController;
import com.example.microservicio_historial_clinico.model.HistorialClinico;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class HistorialClinicoAssembler implements RepresentationModelAssembler<HistorialClinico, EntityModel<HistorialClinico>> {

    @Override
    public EntityModel<HistorialClinico> toModel(HistorialClinico historialClinico) {
        return EntityModel.of(
                historialClinico,
                linkTo(methodOn(HistorialClinicoController.class).obtenerHistorialClinicoPorId(historialClinico.getId())).withSelfRel(),
                linkTo(methodOn(HistorialClinicoController.class).listarHistorialesClinicos()).withRel("historiales-clinicos"),
                linkTo(methodOn(HistorialClinicoController.class).actualizarHistorialClinico(historialClinico.getId(), historialClinico)).withRel("actualizar"),
                linkTo(methodOn(HistorialClinicoController.class).eliminarHistorialClinico(historialClinico.getId())).withRel("eliminar")
        );
    }
}
