package com.examplesaludconecta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class SaludConectaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaludConectaApplication.class, args);
    }

    // Este Bean configura el WebClient apuntando fijamente al microservicio de usuarios
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8081/api/usuarios ")
                .build();
    }
}