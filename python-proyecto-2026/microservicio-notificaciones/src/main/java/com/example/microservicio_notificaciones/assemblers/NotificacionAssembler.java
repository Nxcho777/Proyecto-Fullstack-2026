package com.example.microservicio_notificaciones.assemblers;

import com.example.microservicio_notificaciones.controller.NotificacionController;
import com.example.microservicio_notificaciones.model.Notificacion;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NotificacionAssembler implements RepresentationModelAssembler<Notificacion, EntityModel<Notificacion>> {

    @Override
    public EntityModel<Notificacion> toModel(Notificacion notificacion) {
        return EntityModel.of(
                notificacion,
                linkTo(methodOn(NotificacionController.class).obtenerNotificacionPorId(notificacion.getId())).withSelfRel(),
                linkTo(methodOn(NotificacionController.class).listarNotificaciones()).withRel("notificaciones"),
                linkTo(methodOn(NotificacionController.class).actualizarNotificacion(notificacion.getId(), notificacion)).withRel("actualizar"),
                linkTo(methodOn(NotificacionController.class).eliminarNotificacion(notificacion.getId())).withRel("eliminar")
        );
    }
}
