package com.example.microservicio_notificaciones.controller;

import com.example.microservicio_notificaciones.assemblers.NotificacionAssembler;
import com.example.microservicio_notificaciones.model.Notificacion;
import com.example.microservicio_notificaciones.service.NotificacionService;
import java.time.LocalDate;
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
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificacionController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificacionService notificacionService;

    @MockitoBean
    private NotificacionAssembler notificacionAssembler;

    @Test
    @DisplayName("Debe listar notificaciones con enlaces HATEOAS")
    void shouldListarNotificacionesConHateoas() throws Exception {
        Notificacion notificacion = crearNotificacion(1L);
        EntityModel<Notificacion> modelo = crearModelo(notificacion);

        when(notificacionService.listarNotificaciones()).thenReturn(List.of(notificacion));
        when(notificacionAssembler.toModel(notificacion)).thenReturn(modelo);

        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/notificaciones")))
                .andExpect(jsonPath("$._embedded").exists());

        verify(notificacionService, times(1)).listarNotificaciones();
        verify(notificacionAssembler, times(1)).toModel(notificacion);
    }

    @Test
    @DisplayName("Debe obtener notificacion por ID")
    void shouldObtenerNotificacionPorId() throws Exception {
        Long id = 1L;
        Notificacion notificacion = crearNotificacion(id);
        EntityModel<Notificacion> modelo = crearModelo(notificacion);

        when(notificacionService.obtenerNotificacionPorId(id)).thenReturn(Optional.of(notificacion));
        when(notificacionAssembler.toModel(notificacion)).thenReturn(modelo);

        mockMvc.perform(get("/api/notificaciones/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(notificacionService, times(1)).obtenerNotificacionPorId(id);
        verify(notificacionAssembler, times(1)).toModel(notificacion);
    }

    @Test
    @DisplayName("Debe retornar 404 si no existe")
    void shouldReturnNotFoundWhenNotificacionNoExiste() throws Exception {
        Long id = 99L;

        when(notificacionService.obtenerNotificacionPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/notificaciones/{id}", id))
                .andExpect(status().isNotFound());

        verify(notificacionService, times(1)).obtenerNotificacionPorId(id);
        verify(notificacionAssembler, never()).toModel(any(Notificacion.class));
    }

    @Test
    @DisplayName("Debe crear notificacion")
    void shouldCrearNotificacion() throws Exception {
        Notificacion notificacion = crearNotificacion(1L);
        EntityModel<Notificacion> modelo = crearModelo(notificacion);
        String json = """
{
  "usuarioId": 1,
  "mensaje": "Su cita fue agendada correctamente",
  "canal": "EMAIL",
  "estado": "ENVIADA",
  "fechaEnvio": "2026-07-10"
}
                """;

        when(notificacionService.guardarNotificacion(any(Notificacion.class))).thenReturn(notificacion);
        when(notificacionAssembler.toModel(notificacion)).thenReturn(modelo);

        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(notificacionService, times(1)).guardarNotificacion(any(Notificacion.class));
        verify(notificacionAssembler, times(1)).toModel(notificacion);
    }

    @Test
    @DisplayName("Debe eliminar notificacion existente")
    void shouldEliminarNotificacionExistente() throws Exception {
        Long id = 1L;
        when(notificacionService.eliminarNotificacion(id)).thenReturn(true);

        mockMvc.perform(delete("/api/notificaciones/{id}", id))
                .andExpect(status().isNoContent());

        verify(notificacionService, times(1)).eliminarNotificacion(id);
    }

    @Test
    @DisplayName("Debe retornar 404 al eliminar notificacion inexistente")
    void shouldReturnNotFoundWhenEliminarNotificacionNoExiste() throws Exception {
        Long id = 99L;
        when(notificacionService.eliminarNotificacion(id)).thenReturn(false);

        mockMvc.perform(delete("/api/notificaciones/{id}", id))
                .andExpect(status().isNotFound());

        verify(notificacionService, times(1)).eliminarNotificacion(id);
    }

    private EntityModel<Notificacion> crearModelo(Notificacion notificacion) {
        return EntityModel.of(
                notificacion,
                linkTo(methodOn(NotificacionController.class).obtenerNotificacionPorId(notificacion.getId())).withSelfRel()
        );
    }

    private Notificacion crearNotificacion(Long id) {
        Notificacion notificacion = new Notificacion();
        notificacion.setId(id);
        notificacion.setUsuarioId(1L);
        notificacion.setMensaje("Su cita fue agendada correctamente");
        notificacion.setCanal("EMAIL");
        notificacion.setEstado("ENVIADA");
        notificacion.setFechaEnvio(String.valueOf("2026-07-10"));
        return notificacion;
    }
}
