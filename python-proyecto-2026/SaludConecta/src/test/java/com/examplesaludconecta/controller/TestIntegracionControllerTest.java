package com.examplesaludconecta.controller;

import com.examplesaludconecta.dto.UsuarioExistenciaResponse;
import com.examplesaludconecta.exception.MicroservicioNoDisponibleException;
import com.examplesaludconecta.service.AutenticacionClienteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestIntegracionController.class)
class TestIntegracionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutenticacionClienteService authService;

    @Test
    @DisplayName("Debe verificar correctamente un usuario remoto")
    void debeVerificarUsuarioRemoto() throws Exception {
        String email = "admin@saludconecta.cl";
        UsuarioExistenciaResponse respuesta = new UsuarioExistenciaResponse(
                email,
                true,
                "El usuario se encuentra registrado"
        );

        when(authService.obtenerValidacionUsuario(email)).thenReturn(respuesta);

        mockMvc.perform(get("/api/test-conexion/verificar/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.existe").value(true))
                .andExpect(jsonPath("$.mensaje").value("El usuario se encuentra registrado"));

        verify(authService).obtenerValidacionUsuario(email);
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el correo es inválido")
    void debeRetornar400CuandoCorreoEsInvalido() throws Exception {
        mockMvc.perform(get("/api/test-conexion/verificar/{email}", "correo-invalido"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400));
    }

    @Test
    @DisplayName("Debe retornar 503 cuando el microservicio de usuarios no responde")
    void debeRetornar503CuandoMicroservicioNoDisponible() throws Exception {
        String email = "admin@saludconecta.cl";

        when(authService.obtenerValidacionUsuario(email))
                .thenThrow(new MicroservicioNoDisponibleException(
                        "El microservicio de usuarios no se encuentra disponible"
                ));

        mockMvc.perform(get("/api/test-conexion/verificar/{email}", email))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.estado").value(503))
                .andExpect(jsonPath("$.error").value("Microservicio no disponible"))
                .andExpect(jsonPath("$.mensaje")
                        .value("El microservicio de usuarios no se encuentra disponible"));
    }
}
