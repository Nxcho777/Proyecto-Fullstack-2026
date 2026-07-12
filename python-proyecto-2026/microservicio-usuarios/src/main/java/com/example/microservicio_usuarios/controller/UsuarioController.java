package com.example.microservicio_usuarios.controller;

import com.example.microservicio_usuarios.assemblers.UsuarioAssembler;
import com.example.microservicio_usuarios.dto.UsuarioExistenciaResponse;
import com.example.microservicio_usuarios.exception.ResourceNotFoundException;
import com.example.microservicio_usuarios.model.Usuario;
import com.example.microservicio_usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Validated
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

        if (listaUsuarios.isEmpty()) {
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

    @Operation(summary = "Crear usuario", description = "Registra un nuevo usuario en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "Username o email duplicado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Usuario>> crearUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioAssembler.toModel(nuevoUsuario));
    }

    @Operation(
            summary = "Buscar usuario por ID",
            description = "Obtiene un usuario específico mediante su ID e incluye enlaces HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtenerUsuarioPorId(@PathVariable Integer id) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        return ResponseEntity.ok(usuarioAssembler.toModel(usuario));
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza todos los datos de un usuario mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Username o email duplicado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> actualizarUsuario(
            @PathVariable Integer id,
            @Valid @RequestBody Usuario usuarioActualizado
    ) {
        Usuario usuario = usuarioService.actualizarUsuario(id, usuarioActualizado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        return ResponseEntity.ok(usuarioAssembler.toModel(usuario));
    }

    @Operation(
            summary = "Verificar si el usuario existe",
            description = "Comprueba si un usuario se encuentra registrado mediante su email."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Email inválido", content = @Content)
    })
    @GetMapping("/existe/{email}")
    public ResponseEntity<Boolean> existeUsuario(
            @PathVariable @Email(message = "El email ingresado no tiene un formato válido") String email
    ) {
        boolean existe = usuarioService.existeUsuarioPorEmail(email);
        return ResponseEntity.ok(existe);
    }

    @Operation(
            summary = "Validar usuario para comunicación REST",
            description = "Retorna un DTO con el resultado de la búsqueda por email."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Email inválido", content = @Content)
    })
    @GetMapping("/validar/{email}")
    public ResponseEntity<UsuarioExistenciaResponse> validarUsuario(
            @PathVariable @Email(message = "El email ingresado no tiene un formato válido") String email
    ) {
        boolean existe = usuarioService.existeUsuarioPorEmail(email);
        String mensaje = existe
                ? "El usuario se encuentra registrado"
                : "El usuario no se encuentra registrado";

        return ResponseEntity.ok(new UsuarioExistenciaResponse(email, existe, mensaje));
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario registrado mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado de manera efectiva"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
        boolean eliminado = usuarioService.eliminarUsuario(id);

        if (!eliminado) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }

        return ResponseEntity.noContent().build();
    }
}
