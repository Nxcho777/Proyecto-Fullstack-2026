package com.examplesaludconecta.service;

import com.examplesaludconecta.dto.UsuarioExistenciaResponse;
import com.examplesaludconecta.exception.MicroservicioNoDisponibleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutenticacionClienteServiceTest {

    @Test
    @DisplayName("Debe obtener un DTO desde el microservicio de usuarios")
    void shouldObtenerValidacionUsuario() {
        WebClient webClient = crearWebClient(
                HttpStatus.OK,
                """
                {
                    "email": "admin@saludconecta.cl",
                    "existe": true,
                    "mensaje": "El usuario se encuentra registrado"
                }
                """
        );

        AutenticacionClienteService authService = new AutenticacionClienteService();
        ReflectionTestUtils.setField(authService, "webClient", webClient);

        UsuarioExistenciaResponse respuesta =
                authService.obtenerValidacionUsuario("admin@saludconecta.cl");

        assertEquals("admin@saludconecta.cl", respuesta.getEmail());
        assertTrue(respuesta.isExiste());
        assertEquals("El usuario se encuentra registrado", respuesta.getMensaje());
    }

    @Test
    @DisplayName("Debe conservar el método booleano de validación")
    void shouldValidarUsuarioEnMicroservicio() {
        WebClient webClient = crearWebClient(
                HttpStatus.OK,
                """
                {
                    "email": "admin@saludconecta.cl",
                    "existe": true,
                    "mensaje": "El usuario se encuentra registrado"
                }
                """
        );

        AutenticacionClienteService authService = new AutenticacionClienteService();
        ReflectionTestUtils.setField(authService, "webClient", webClient);

        assertTrue(authService.validarUsuarioEnMicroservicio("admin@saludconecta.cl"));
    }

    @Test
    @DisplayName("Debe controlar errores remotos y lanzar excepción de servicio no disponible")
    void shouldThrowExceptionWhenMicroservicioFalla() {
        WebClient webClient = crearWebClient(
                HttpStatus.SERVICE_UNAVAILABLE,
                "{\"error\":\"Servicio no disponible\"}"
        );

        AutenticacionClienteService authService = new AutenticacionClienteService();
        ReflectionTestUtils.setField(authService, "webClient", webClient);

        assertThrows(
                MicroservicioNoDisponibleException.class,
                () -> authService.obtenerValidacionUsuario("admin@saludconecta.cl")
        );
    }

    private WebClient crearWebClient(HttpStatus estado, String cuerpo) {
        return WebClient.builder()
                .exchangeFunction(request -> Mono.just(
                        ClientResponse.create(estado)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(cuerpo)
                                .build()
                ))
                .build();
    }
}
