package com.examplesaludconecta;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class SaludConectaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaludConectaApplication.class, args);
    }

    // Este Bean configura el WebClient apuntando al microservicio de usuarios.
    @Bean
    public WebClient webClient(@Value("${microservicio.usuarios.url}") String microservicioUsuariosUrl) {
        return WebClient.builder()
                .baseUrl(microservicioUsuariosUrl)
                .build();
    }
}
