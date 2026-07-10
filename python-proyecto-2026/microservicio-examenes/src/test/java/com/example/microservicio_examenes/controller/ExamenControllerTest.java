package com.example.microservicio_examenes.controller;

import com.example.microservicio_examenes.assemblers.ExamenAssembler;
import com.example.microservicio_examenes.model.Examen;
import com.example.microservicio_examenes.service.ExamenService;
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

@WebMvcTest(ExamenController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExamenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExamenService examenService;

    @MockitoBean
    private ExamenAssembler examenAssembler;

    @Test
    @DisplayName("Debe listar examenes con enlaces HATEOAS")
    void shouldListarExamenesConHateoas() throws Exception {
        Examen examen = crearExamen(1L);
        EntityModel<Examen> modelo = crearModelo(examen);

        when(examenService.listarExamenes()).thenReturn(List.of(examen));
        when(examenAssembler.toModel(examen)).thenReturn(modelo);

        mockMvc.perform(get("/api/examenes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/examenes")))
                .andExpect(jsonPath("$._embedded").exists());

        verify(examenService, times(1)).listarExamenes();
        verify(examenAssembler, times(1)).toModel(examen);
    }

    @Test
    @DisplayName("Debe obtener examen por ID")
    void shouldObtenerExamenPorId() throws Exception {
        Long id = 1L;
        Examen examen = crearExamen(id);
        EntityModel<Examen> modelo = crearModelo(examen);

        when(examenService.obtenerExamenPorId(id)).thenReturn(Optional.of(examen));
        when(examenAssembler.toModel(examen)).thenReturn(modelo);

        mockMvc.perform(get("/api/examenes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(examenService, times(1)).obtenerExamenPorId(id);
        verify(examenAssembler, times(1)).toModel(examen);
    }

    @Test
    @DisplayName("Debe retornar 404 si no existe")
    void shouldReturnNotFoundWhenExamenNoExiste() throws Exception {
        Long id = 99L;

        when(examenService.obtenerExamenPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/examenes/{id}", id))
                .andExpect(status().isNotFound());

        verify(examenService, times(1)).obtenerExamenPorId(id);
        verify(examenAssembler, never()).toModel(any(Examen.class));
    }

    @Test
    @DisplayName("Debe crear examen")
    void shouldCrearExamen() throws Exception {
        Examen examen = crearExamen(1L);
        EntityModel<Examen> modelo = crearModelo(examen);
        String json = """
{
  "pacienteId": 1,
  "tipoExamen": "Hemograma",
  "resultado": "Pendiente",
  "estado": "SOLICITADO",
  "fechaSolicitud": "2026-07-10"
}
                """;

        when(examenService.guardarExamen(any(Examen.class))).thenReturn(examen);
        when(examenAssembler.toModel(examen)).thenReturn(modelo);

        mockMvc.perform(post("/api/examenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(examenService, times(1)).guardarExamen(any(Examen.class));
        verify(examenAssembler, times(1)).toModel(examen);
    }

    @Test
    @DisplayName("Debe eliminar examen existente")
    void shouldEliminarExamenExistente() throws Exception {
        Long id = 1L;
        when(examenService.eliminarExamen(id)).thenReturn(true);

        mockMvc.perform(delete("/api/examenes/{id}", id))
                .andExpect(status().isNoContent());

        verify(examenService, times(1)).eliminarExamen(id);
    }

    @Test
    @DisplayName("Debe retornar 404 al eliminar examen inexistente")
    void shouldReturnNotFoundWhenEliminarExamenNoExiste() throws Exception {
        Long id = 99L;
        when(examenService.eliminarExamen(id)).thenReturn(false);

        mockMvc.perform(delete("/api/examenes/{id}", id))
                .andExpect(status().isNotFound());

        verify(examenService, times(1)).eliminarExamen(id);
    }

    private EntityModel<Examen> crearModelo(Examen examen) {
        return EntityModel.of(
                examen,
                linkTo(methodOn(ExamenController.class).obtenerExamenPorId(examen.getId())).withSelfRel()
        );
    }

    private Examen crearExamen(Long id) {
        Examen examen = new Examen();
        examen.setId(id);
        examen.setPacienteId(1L);
        examen.setTipoExamen("Hemograma");
        examen.setResultado("Pendiente");
        examen.setEstado("SOLICITADO");
        examen.setFechaSolicitud(String.valueOf(LocalDate.parse("2026-07-10")));
        return examen;
    }
}
