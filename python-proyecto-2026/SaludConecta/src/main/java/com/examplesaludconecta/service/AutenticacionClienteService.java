package com.examplesaludconecta.service;

import com.examplesaludconecta.dto.UsuarioExistenciaResponse;
import com.examplesaludconecta.exception.MicroservicioNoDisponibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.time.Duration;

@Service
public class AutenticacionClienteService {

    private static final Logger log = LoggerFactory.getLogger(AutenticacionClienteService.class);

    @Autowired
    private WebClient webClient;

    public UsuarioExistenciaResponse obtenerValidacionUsuario(String email) {
        try {
            UsuarioExistenciaResponse respuesta = webClient.get()
                    .uri("/validar/{email}", email)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> response.createException())
                    .bodyToMono(UsuarioExistenciaResponse.class)
                    .block(Duration.ofSeconds(5));

            if (respuesta == null) {
                throw new MicroservicioNoDisponibleException(
                        "El microservicio de usuarios respondió sin contenido"
                );
            }

            log.info("Consulta remota completada para el correo {}", email);
            return respuesta;
        } catch (MicroservicioNoDisponibleException ex) {
            throw ex;
        } catch (WebClientException ex) {
            log.error("Error HTTP al conectar con el microservicio de usuarios: {}", ex.getMessage());
            throw new MicroservicioNoDisponibleException(
                    "No fue posible obtener una respuesta válida del microservicio de usuarios"
            );
        } catch (Exception ex) {
            log.error("Error inesperado al conectar con el microservicio de usuarios", ex);
            throw new MicroservicioNoDisponibleException(
                    "El microservicio de usuarios no se encuentra disponible"
            );
        }
    }

    public boolean validarUsuarioEnMicroservicio(String username) {
        return obtenerValidacionUsuario(username).isExiste();
    }
}
