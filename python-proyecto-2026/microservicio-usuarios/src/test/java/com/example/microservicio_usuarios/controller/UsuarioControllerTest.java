package com.example.microservicio_usuarios.controller;

import com.example.microservicio_usuarios.assemblers.UsuarioAssembler;
import com.example.microservicio_usuarios.config.JwtAuthenticationFilter;
import com.example.microservicio_usuarios.model.Usuario;
import com.example.microservicio_usuarios.service.JwtService;
import com.example.microservicio_usuarios.service.UsuarioService;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private UsuarioAssembler usuarioAssembler;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker();
    }

    @Test
    @DisplayName("Debe listar usuarios con enlaces HATEOAS")
    void shouldListarUsuariosConHateoas() throws Exception {
        Usuario usuario = crearUsuario(1);
        EntityModel<Usuario> usuarioModel = crearModelo(usuario);

        when(usuarioService.listarUsuarios()).thenReturn(List.of(usuario));
        when(usuarioAssembler.toModel(usuario)).thenReturn(usuarioModel);

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/usuarios")))
                .andExpect(jsonPath("$._embedded").exists());

        verify(usuarioService, times(1)).listarUsuarios();
        verify(usuarioAssembler, times(1)).toModel(usuario);
    }

    @Test
    @DisplayName("Debe retornar 204 si no existen usuarios")
    void shouldReturnNoContentWhenListaVacia() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of());

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Debe crear un usuario")
    void shouldCrearUsuario() throws Exception {
        Usuario usuario = crearUsuario(1);
        EntityModel<Usuario> usuarioModel = crearModelo(usuario);

        when(usuarioService.guardarUsuario(any(Usuario.class))).thenReturn(usuario);
        when(usuarioAssembler.toModel(usuario)).thenReturn(usuarioModel);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUsuario(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()));
    }

    @Test
    @DisplayName("Debe obtener un usuario por ID con enlaces HATEOAS")
    void shouldObtenerUsuarioPorIdConHateoas() throws Exception {
        Integer id = 1;
        Usuario usuario = crearUsuario(id);
        EntityModel<Usuario> usuarioModel = crearModelo(usuario);

        when(usuarioService.obtenerUsuarioPorId(id)).thenReturn(Optional.of(usuario));
        when(usuarioAssembler.toModel(usuario)).thenReturn(usuarioModel);

        mockMvc.perform(get("/api/usuarios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("Debe retornar 404 si el usuario no existe")
    void shouldReturnNotFoundWhenUsuarioNoExiste() throws Exception {
        Integer id = 99;
        when(usuarioService.obtenerUsuarioPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/{id}", id))
                .andExpect(status().isNotFound());

        verify(usuarioAssembler, never()).toModel(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe actualizar un usuario")
    void shouldActualizarUsuario() throws Exception {
        Usuario usuario = crearUsuario(1);
        EntityModel<Usuario> usuarioModel = crearModelo(usuario);

        when(usuarioService.actualizarUsuario(any(Integer.class), any(Usuario.class)))
                .thenReturn(Optional.of(usuario));
        when(usuarioAssembler.toModel(usuario)).thenReturn(usuarioModel);

        mockMvc.perform(put("/api/usuarios/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUsuario(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Debe retornar true si el usuario existe por email")
    void shouldExisteUsuarioPorEmail() throws Exception {
        Usuario usuario = crearUsuario(1);
        when(usuarioService.existeUsuarioPorEmail(usuario.getEmail())).thenReturn(true);

        mockMvc.perform(get("/api/usuarios/existe/{email}", usuario.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("Debe retornar DTO para la comunicación distribuida")
    void shouldValidarUsuarioConDto() throws Exception {
        Usuario usuario = crearUsuario(1);
        when(usuarioService.existeUsuarioPorEmail(usuario.getEmail())).thenReturn(true);

        mockMvc.perform(get("/api/usuarios/validar/{email}", usuario.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(usuario.getEmail()))
                .andExpect(jsonPath("$.existe").value(true))
                .andExpect(jsonPath("$.mensaje").value("El usuario se encuentra registrado"));
    }

    @Test
    @DisplayName("Debe eliminar un usuario existente")
    void shouldEliminarUsuarioExistente() throws Exception {
        when(usuarioService.eliminarUsuario(1)).thenReturn(true);

        mockMvc.perform(delete("/api/usuarios/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Debe retornar 404 al eliminar un usuario inexistente")
    void shouldReturnNotFoundWhenEliminarUsuarioNoExiste() throws Exception {
        when(usuarioService.eliminarUsuario(99)).thenReturn(false);

        mockMvc.perform(delete("/api/usuarios/{id}", 99))
                .andExpect(status().isNotFound());
    }

    private EntityModel<Usuario> crearModelo(Usuario usuario) {
        return EntityModel.of(
                usuario,
                linkTo(methodOn(UsuarioController.class)
                        .obtenerUsuarioPorId(usuario.getId())).withSelfRel()
        );
    }

    private Usuario crearUsuario(Integer id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUsername(faker.internet().username());
        usuario.setPassword("123456");
        usuario.setEmail(faker.internet().emailAddress());
        usuario.setRol("USER");
        return usuario;
    }

    private String jsonUsuario(Usuario usuario) {
        return """
                {
                    "username": "%s",
                    "password": "%s",
                    "email": "%s",
                    "rol": "%s"
                }
                """.formatted(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getEmail(),
                usuario.getRol()
        );
    }
}
