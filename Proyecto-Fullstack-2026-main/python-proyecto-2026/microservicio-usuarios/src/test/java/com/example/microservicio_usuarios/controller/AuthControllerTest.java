package com.example.microservicio_usuarios.controller;

import com.example.microservicio_usuarios.model.Usuario;
import com.example.microservicio_usuarios.repository.UsuarioRepository;
import com.example.microservicio_usuarios.service.JwtService;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private JwtService jwtService;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker();
    }

    @Test
    @DisplayName("Debe iniciar sesión correctamente y retornar token")
    void shouldLoginCorrectamente() throws Exception {
        Usuario usuario = crearUsuario(1);
        String token = "token-fake-jwt";

        when(usuarioRepository.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));

        when(jwtService.generarToken(usuario.getEmail()))
                .thenReturn(token);

        String body = """
                {
                    "email": "%s",
                    "password": "123456"
                }
                """.formatted(usuario.getEmail());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("El usuario ha iniciado sesión exitosamente"))
                .andExpect(jsonPath("$.token").value(token));

        verify(usuarioRepository, times(1)).findByEmail(usuario.getEmail());
        verify(jwtService, times(1)).generarToken(usuario.getEmail());
    }

    @Test
    @DisplayName("Debe retornar 401 si el email no está registrado")
    void shouldReturnUnauthorizedWhenEmailNoExiste() throws Exception {
        String email = faker.internet().emailAddress();

        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        String body = """
                {
                    "email": "%s",
                    "password": "123456"
                }
                """.formatted(email);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.Error").value("El email ingresado no está registrado o no existe"));

        verify(usuarioRepository, times(1)).findByEmail(email);
        verify(jwtService, never()).generarToken(anyString());
    }

    @Test
    @DisplayName("Debe retornar 401 si la contraseña es incorrecta")
    void shouldReturnUnauthorizedWhenPasswordIncorrecta() throws Exception {
        Usuario usuario = crearUsuario(1);

        when(usuarioRepository.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));

        String body = """
                {
                    "email": "%s",
                    "password": "contraseñaIncorrecta"
                }
                """.formatted(usuario.getEmail());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.Error").value("La contraseña ingresada es incorrecta"));

        verify(usuarioRepository, times(1)).findByEmail(usuario.getEmail());
        verify(jwtService, never()).generarToken(anyString());
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