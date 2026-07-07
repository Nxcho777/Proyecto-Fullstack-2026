package com.example.microservicio_usuarios.controller;

import com.example.microservicio_usuarios.assemblers.UsuarioAssembler;
import com.example.microservicio_usuarios.model.Usuario;
import com.example.microservicio_usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/usuarios")
@Tag(
        name = "Controlador de Usuarios",
        description = "Operaciones relacionadas con la gestión y validación de usuarios"
)
public class UsuarioController {

@Autowired
private UsuarioService usuarioService;

    @Autowired
    private UsuarioAssembler usuarioAssembler;

    @Operation(
            summary = "Listar usuarios",
            description = "Obtiene todos los usuarios registrados e incluye enlaces HATEOAS."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios encontrados de manera correcta"),
        @ApiResponse(responseCode = "204", description = "No existen usuarios registrados", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Usuario>>> listarUsuarios() {

        List<Usuario> listaUsuarios = usuarioService.listarUsuarios();

        if (listaUsuarios.isEmpty()){
                return ResponseEntity.noContent().build();
        }
        List<EntityModel<Usuario>> usuarios = listaUsuarios
                .stream()
                .map(usuarioAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Usuario>> respuesta = CollectionModel.of(
                usuarios,
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel()
        );
        
        return ResponseEntity.ok(respuesta);
    }

    @Operation(
            summary = "Buscar usuario por ID",
            description = "Obtiene un usuario específico mediante su ID e incluye enlaces HATEOAS."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios encontrados correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuarios no encontrados", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtenerUsuarioPorId(@PathVariable Integer id) {

        return usuarioService.obtenerUsuarioPorId(id)
                .map(usuarioAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Verificar si el usuario existe",
            description = "Comprueba si un usuario si se encuentra registrado en el sistema mediante su email."
    )
     @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuarios buscado por gmail no se ha encontrado", content = @Content)
     })     
    @GetMapping("/existe/{email}")
        public ResponseEntity<Boolean> existeUsuario(@PathVariable String email) {
                boolean existe = usuarioService.existeUsuarioPorEmail(email);
                return ResponseEntity.ok(existe);
        }

    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario registrado mediante su ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios eliminado de manera efectiva"),
        @ApiResponse(responseCode = "404", description = "Usuarios no encontrados", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
        boolean eliminado = usuarioService.eliminarUsuario(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
} 
}