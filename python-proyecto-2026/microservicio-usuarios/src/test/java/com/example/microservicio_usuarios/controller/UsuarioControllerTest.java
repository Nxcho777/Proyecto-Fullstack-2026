package com.example.microservicio_usuarios.controller;

import com.example.microservicio_usuarios.service.JwtService;
import com.example.microservicio_usuarios.assemblers.UsuarioAssembler;
import com.example.microservicio_usuarios.model.Usuario;
import com.example.microservicio_usuarios.repository.UsuarioRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private UsuarioAssembler usuarioAssembler;

    @MockitoBean
    private JwtService jwtService;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker();
    }

    @Test
    @DisplayName("Debe listar usuarios con enlaces HATEOAS")
    void shouldListarUsuariosConHateoas() throws Exception {
        Usuario usuario = crearUsuario(1);

        EntityModel<Usuario> usuarioModel = EntityModel.of(
                usuario,
                linkTo(methodOn(UsuarioController.class)
                        .obtenerUsuarioPorId(usuario.getId())).withSelfRel()
        );

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
        when(usuarioAssembler.toModel(usuario)).thenReturn(usuarioModel);

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/usuarios")))
                .andExpect(jsonPath("$._embedded").exists());

        verify(usuarioRepository, times(1)).findAll();
        verify(usuarioAssembler, times(1)).toModel(usuario);
    }

    @Test
    @DisplayName("Debe obtener un usuario por ID con enlaces HATEOAS")
    void shouldObtenerUsuarioPorIdConHateoas() throws Exception {
        Integer id = 1;
        Usuario usuario = crearUsuario(id);

        EntityModel<Usuario> usuarioModel = EntityModel.of(
                usuario,
                linkTo(methodOn(UsuarioController.class)
                        .obtenerUsuarioPorId(id)).withSelfRel()
        );

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioAssembler.toModel(usuario)).thenReturn(usuarioModel);

        mockMvc.perform(get("/api/usuarios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/usuarios/" + id)));

        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioAssembler, times(1)).toModel(usuario);
    }

    @Test
    @DisplayName("Debe retornar 404 si el usuario no existe")
    void shouldReturnNotFoundWhenUsuarioNoExiste() throws Exception {
        Integer id = 99;

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/{id}", id))
                .andExpect(status().isNotFound());

        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioAssembler, never()).toModel(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe retornar true si el usuario existe por email")
    void shouldExisteUsuarioPorEmail() throws Exception {
        Usuario usuario = crearUsuario(1);

        when(usuarioRepository.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuarios/existe/{email}", usuario.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(usuarioRepository, times(1)).findByEmail(usuario.getEmail());
    }

    @Test
    @DisplayName("Debe eliminar un usuario existente")
    void shouldEliminarUsuarioExistente() throws Exception {
        Integer id = 1;

        when(usuarioRepository.existsById(id)).thenReturn(true);

        mockMvc.perform(delete("/api/usuarios/{id}", id))
                .andExpect(status().isNoContent());

        verify(usuarioRepository, times(1)).existsById(id);
        verify(usuarioRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Debe retornar 404 al eliminar un usuario inexistente")
    void shouldReturnNotFoundWhenEliminarUsuarioNoExiste() throws Exception {
        Integer id = 99;

        when(usuarioRepository.existsById(id)).thenReturn(false);

        mockMvc.perform(delete("/api/usuarios/{id}", id))
                .andExpect(status().isNotFound());

        verify(usuarioRepository, times(1)).existsById(id);
        verify(usuarioRepository, never()).deleteById(id);
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
}