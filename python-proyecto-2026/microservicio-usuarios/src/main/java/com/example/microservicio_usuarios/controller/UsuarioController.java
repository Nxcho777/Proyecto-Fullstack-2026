package com.example.microservicio_usuarios.controller;

import com.example.microservicio_usuarios.assemblers.UsuarioAssembler;
import com.example.microservicio_usuarios.model.Usuario;
import com.example.microservicio_usuarios.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/usuarios")
@Tag(
        name = "Controlador de Usuarios",
        description = "Operaciones relacionadas con la gestión y validación de usuarios"
)
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioAssembler usuarioAssembler;

    @Operation(
            summary = "Listar usuarios",
            description = "Obtiene todos los usuarios registrados e incluye enlaces HATEOAS."
    )
    @GetMapping
    public CollectionModel<EntityModel<Usuario>> listarUsuarios() {

        List<EntityModel<Usuario>> usuarios = usuarioRepository.findAll()
                .stream()
                .map(usuarioAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(
                usuarios,
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel()
        );
    }

    @Operation(
            summary = "Buscar usuario por ID",
            description = "Obtiene un usuario específico mediante su ID e incluye enlaces HATEOAS."
    )
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtenerUsuarioPorId(@PathVariable Integer id) {

        return usuarioRepository.findById(id)
                .map(usuarioAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Verificar si el usuario existe",
            description = "Comprueba si un usuario si se encuentra registrado en el sistema mediante su email."
    )
    @GetMapping("/existe/{email}")
    public ResponseEntity<Boolean> existeUsuario(@PathVariable String email) {

        boolean existe = usuarioRepository
                .findByEmail(email)
                .isPresent();

        return ResponseEntity.ok(existe);
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario registrado mediante su ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {

        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}