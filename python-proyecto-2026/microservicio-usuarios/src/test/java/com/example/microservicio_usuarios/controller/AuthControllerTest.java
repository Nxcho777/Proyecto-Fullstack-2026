package com.example.microservicio_usuarios.controller;

import com.example.microservicio_usuarios.config.JwtAuthenticationFilter;
import com.example.microservicio_usuarios.exception.CredencialesInvalidasException;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

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
    @DisplayName("Debe iniciar sesión correctamente y retornar token")
    void shouldLoginCorrectamente() throws Exception {
        Usuario usuario = crearUsuario();
        String token = "token-jwt-de-prueba";

        when(usuarioService.autenticarUsuario(usuario.getEmail(), "123456"))
                .thenReturn(usuario);
        when(jwtService.generarToken(usuario.getEmail()))
                .thenReturn(token);

        String jsonLogin = """
                {
                    "email": "%s",
                    "password": "123456"
                }
                """.formatted(usuario.getEmail());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("El usuario ha iniciado sesión exitosamente"))
                .andExpect(jsonPath("$.token").value(token));

        verify(usuarioService).autenticarUsuario(usuario.getEmail(), "123456");
        verify(jwtService).generarToken(usuario.getEmail());
    }

    @Test
    @DisplayName("Debe retornar 401 si el email no existe")
    void shouldReturnUnauthorizedWhenEmailNoExiste() throws Exception {
        String email = faker.internet().emailAddress();

        when(usuarioService.autenticarUsuario(email, "123456"))
                .thenThrow(new CredencialesInvalidasException(
                        "El email ingresado no está registrado o no existe"
                ));

        String jsonLogin = """
                {
                    "email": "%s",
                    "password": "123456"
                }
                """.formatted(email);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.estado").value(401))
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"))
                .andExpect(jsonPath("$.mensaje").value("El email ingresado no está registrado o no existe"));

        verify(jwtService, never()).generarToken(anyString());
    }

    @Test
    @DisplayName("Debe retornar 401 si la contraseña es incorrecta")
    void shouldReturnUnauthorizedWhenPasswordIncorrecta() throws Exception {
        Usuario usuario = crearUsuario();

        when(usuarioService.autenticarUsuario(usuario.getEmail(), "claveIncorrecta"))
                .thenThrow(new CredencialesInvalidasException(
                        "La contraseña ingresada es incorrecta"
                ));

        String jsonLogin = """
                {
                    "email": "%s",
                    "password": "claveIncorrecta"
                }
                """.formatted(usuario.getEmail());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("La contraseña ingresada es incorrecta"));

        verify(jwtService, never()).generarToken(anyString());
    }

    @Test
    @DisplayName("Debe retornar 400 si los datos del login son inválidos")
    void shouldReturnBadRequestWhenLoginInvalido() throws Exception {
        String jsonLogin = """
                {
                    "email": "correo-invalido",
                    "password": "123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.detalles.email").exists())
                .andExpect(jsonPath("$.detalles.password").exists());
    }

    private Usuario crearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setUsername(faker.internet().username());
        usuario.setEmail(faker.internet().emailAddress());
        usuario.setPassword("123456");
        usuario.setRol("USER");
        return usuario;
    }
}
