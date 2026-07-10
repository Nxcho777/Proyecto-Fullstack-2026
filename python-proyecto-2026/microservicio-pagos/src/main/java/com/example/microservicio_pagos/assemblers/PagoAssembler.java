package com.example.microservicio_pagos.assemblers;

import com.example.microservicio_pagos.controller.PagoController;
import com.example.microservicio_pagos.model.Pago;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PagoAssembler implements RepresentationModelAssembler<Pago, EntityModel<Pago>> {

    @Override
    public EntityModel<Pago> toModel(Pago pago) {
        return EntityModel.of(
                pago,
                linkTo(methodOn(PagoController.class).obtenerPagoPorId(pago.getId())).withSelfRel(),
                linkTo(methodOn(PagoController.class).listarPagos()).withRel("pagos"),
                linkTo(methodOn(PagoController.class).actualizarPago(pago.getId(), pago)).withRel("actualizar"),
                linkTo(methodOn(PagoController.class).eliminarPago(pago.getId())).withRel("eliminar")
        );
    }
}
