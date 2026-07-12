package com.example.microservicio_usuarios.assemblers;

import com.example.microservicio_usuarios.controller.UsuarioController;
import com.example.microservicio_usuarios.model.Usuario;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UsuarioAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {
        return EntityModel.of(
                usuario,
                linkTo(methodOn(UsuarioController.class)
                        .obtenerUsuarioPorId(usuario.getId()))
                        .withSelfRel(),
                linkTo(methodOn(UsuarioController.class)
                        .listarUsuarios())
                        .withRel("usuarios"),
                linkTo(methodOn(UsuarioController.class)
                        .actualizarUsuario(usuario.getId(), usuario))
                        .withRel("actualizar"),
                linkTo(methodOn(UsuarioController.class)
                        .existeUsuario(usuario.getEmail()))
                        .withRel("verificar-existencia"),
                linkTo(methodOn(UsuarioController.class)
                        .eliminarUsuario(usuario.getId()))
                        .withRel("eliminar")
        );
    }
}
