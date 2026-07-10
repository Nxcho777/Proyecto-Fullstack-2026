package com.example.microservicio_diagnosticos.assemblers;

import com.example.microservicio_diagnosticos.controller.DiagnosticoController;
import com.example.microservicio_diagnosticos.model.Diagnostico;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DiagnosticoAssembler implements RepresentationModelAssembler<Diagnostico, EntityModel<Diagnostico>> {

    @Override
    public EntityModel<Diagnostico> toModel(Diagnostico diagnostico) {
        return EntityModel.of(
                diagnostico,
                linkTo(methodOn(DiagnosticoController.class).obtenerDiagnosticoPorId(diagnostico.getId())).withSelfRel(),
                linkTo(methodOn(DiagnosticoController.class).listarDiagnosticos()).withRel("diagnosticos"),
                linkTo(methodOn(DiagnosticoController.class).actualizarDiagnostico(diagnostico.getId(), diagnostico)).withRel("actualizar"),
                linkTo(methodOn(DiagnosticoController.class).eliminarDiagnostico(diagnostico.getId())).withRel("eliminar")
        );
    }
}
