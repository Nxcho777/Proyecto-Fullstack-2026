package com.example.microservicio_citas.controller;

import com.example.microservicio_citas.assemblers.CitaAssembler;
import com.example.microservicio_citas.model.Cita;
import com.example.microservicio_citas.service.CitaService;
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

@WebMvcTest(CitaController.class)
@AutoConfigureMockMvc(addFilters = false)
class CitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CitaService citaService;

    @MockitoBean
    private CitaAssembler citaAssembler;

    @Test
    @DisplayName("Debe listar citas con enlaces HATEOAS")
    void shouldListarCitasConHateoas() throws Exception {
        Cita cita = crearCita(1L);
        EntityModel<Cita> modelo = crearModelo(cita);

        when(citaService.listarCitas()).thenReturn(List.of(cita));
        when(citaAssembler.toModel(cita)).thenReturn(modelo);

        mockMvc.perform(get("/api/citas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/citas")))
                .andExpect(jsonPath("$._embedded").exists());

        verify(citaService, times(1)).listarCitas();
        verify(citaAssembler, times(1)).toModel(cita);
    }

    @Test
    @DisplayName("Debe obtener cita por ID")
    void shouldObtenerCitaPorId() throws Exception {
        Long id = 1L;
        Cita cita = crearCita(id);
        EntityModel<Cita> modelo = crearModelo(cita);

        when(citaService.obtenerCitaPorId(id)).thenReturn(Optional.of(cita));
        when(citaAssembler.toModel(cita)).thenReturn(modelo);

        mockMvc.perform(get("/api/citas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(citaService, times(1)).obtenerCitaPorId(id);
        verify(citaAssembler, times(1)).toModel(cita);
    }

    @Test
    @DisplayName("Debe retornar 404 si no existe")
    void shouldReturnNotFoundWhenCitaNoExiste() throws Exception {
        Long id = 99L;

        when(citaService.obtenerCitaPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/citas/{id}", id))
                .andExpect(status().isNotFound());

        verify(citaService, times(1)).obtenerCitaPorId(id);
        verify(citaAssembler, never()).toModel(any(Cita.class));
    }

    @Test
    @DisplayName("Debe crear cita")
    void shouldCrearCita() throws Exception {
        Cita cita = crearCita(1L);
        EntityModel<Cita> modelo = crearModelo(cita);
        String json = """
{
  "pacienteId": 1,
  "medicoId": 1,
  "fecha": "2026-07-10",
  "hora": "10:30",
  "motivo": "Control general",
  "estado": "AGENDADA"
}
                """;

        when(citaService.guardarCita(any(Cita.class))).thenReturn(cita);
        when(citaAssembler.toModel(cita)).thenReturn(modelo);

        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(citaService, times(1)).guardarCita(any(Cita.class));
        verify(citaAssembler, times(1)).toModel(cita);
    }

    @Test
    @DisplayName("Debe eliminar cita existente")
    void shouldEliminarCitaExistente() throws Exception {
        Long id = 1L;
        when(citaService.eliminarCita(id)).thenReturn(true);

        mockMvc.perform(delete("/api/citas/{id}", id))
                .andExpect(status().isNoContent());

        verify(citaService, times(1)).eliminarCita(id);
    }

    @Test
    @DisplayName("Debe retornar 404 al eliminar cita inexistente")
    void shouldReturnNotFoundWhenEliminarCitaNoExiste() throws Exception {
        Long id = 99L;
        when(citaService.eliminarCita(id)).thenReturn(false);

        mockMvc.perform(delete("/api/citas/{id}", id))
                .andExpect(status().isNotFound());

        verify(citaService, times(1)).eliminarCita(id);
    }

    private EntityModel<Cita> crearModelo(Cita cita) {
        return EntityModel.of(
                cita,
                linkTo(methodOn(CitaController.class).obtenerCitaPorId(cita.getId())).withSelfRel()
        );
    }

    private Cita crearCita(Long id) {
        Cita cita = new Cita();
        cita.setId(id);
        cita.setPacienteId(1L);
        cita.setMedicoId(1L);
        cita.setFecha("2026-07-10");
        cita.setHora("10:30");
        cita.setMotivo("Control general");
        cita.setEstado("AGENDADA");
        return cita;
    }
}
