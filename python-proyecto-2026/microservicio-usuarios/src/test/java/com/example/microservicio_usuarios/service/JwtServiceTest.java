package com.example.microservicio_usuarios.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                "SaludConectaSecretKeyProyectoFullstack2026DSY1103MuySegura"
        );
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L);
    }

    @Test
    @DisplayName("Debe generar, leer y validar un token JWT")
    void shouldGenerarYValidarToken() {
        String email = "admin@saludconecta.cl";

        String token = jwtService.generarToken(email);

        assertEquals(email, jwtService.extraerUsername(token));
        assertFalse(jwtService.tokenExpirado(token));
        assertTrue(jwtService.validarToken(token, email));
    }

    @Test
    @DisplayName("Debe detectar cuando el username del token no coincide")
    void shouldRejectUsernameDiferente() {
        String token = jwtService.generarToken("admin@saludconecta.cl");

        assertFalse(jwtService.validarToken(token, "otro@saludconecta.cl"));
    }

    @Test
    @DisplayName("Debe detectar un token expirado")
    void shouldDetectTokenExpirado() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);
        String token = jwtService.generarToken("admin@saludconecta.cl");

        assertTrue(jwtService.tokenExpirado(token));
    }
}
