package com.example.microservicio_recetas.assemblers;

import com.example.microservicio_recetas.controller.RecetaController;
import com.example.microservicio_recetas.model.Receta;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RecetaAssembler implements RepresentationModelAssembler<Receta, EntityModel<Receta>> {

    @Override
    public EntityModel<Receta> toModel(Receta receta) {
        return EntityModel.of(
                receta,
                linkTo(methodOn(RecetaController.class).obtenerRecetaPorId(receta.getId())).withSelfRel(),
                linkTo(methodOn(RecetaController.class).listarRecetas()).withRel("recetas"),
                linkTo(methodOn(RecetaController.class).actualizarReceta(receta.getId(), receta)).withRel("actualizar"),
                linkTo(methodOn(RecetaController.class).eliminarReceta(receta.getId())).withRel("eliminar")
        );
    }
}
