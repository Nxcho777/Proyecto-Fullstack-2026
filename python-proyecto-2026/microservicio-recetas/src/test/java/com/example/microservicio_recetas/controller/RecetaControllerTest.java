package com.example.microservicio_recetas.controller;

import com.example.microservicio_recetas.assemblers.RecetaAssembler;
import com.example.microservicio_recetas.model.Receta;
import com.example.microservicio_recetas.service.RecetaService;
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

@WebMvcTest(RecetaController.class)
@AutoConfigureMockMvc(addFilters = false)
class RecetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecetaService recetaService;

    @MockitoBean
    private RecetaAssembler recetaAssembler;

    @Test
    @DisplayName("Debe listar recetas con enlaces HATEOAS")
    void shouldListarRecetasConHateoas() throws Exception {
        Receta receta = crearReceta(1L);
        EntityModel<Receta> modelo = crearModelo(receta);

        when(recetaService.listarRecetas()).thenReturn(List.of(receta));
        when(recetaAssembler.toModel(receta)).thenReturn(modelo);

        mockMvc.perform(get("/api/recetas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/recetas")))
                .andExpect(jsonPath("$._embedded").exists());

        verify(recetaService, times(1)).listarRecetas();
        verify(recetaAssembler, times(1)).toModel(receta);
    }

    @Test
    @DisplayName("Debe obtener receta por ID")
    void shouldObtenerRecetaPorId() throws Exception {
        Long id = 1L;
        Receta receta = crearReceta(id);
        EntityModel<Receta> modelo = crearModelo(receta);

        when(recetaService.obtenerRecetaPorId(id)).thenReturn(Optional.of(receta));
        when(recetaAssembler.toModel(receta)).thenReturn(modelo);

        mockMvc.perform(get("/api/recetas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(recetaService, times(1)).obtenerRecetaPorId(id);
        verify(recetaAssembler, times(1)).toModel(receta);
    }

    @Test
    @DisplayName("Debe retornar 404 si no existe")
    void shouldReturnNotFoundWhenRecetaNoExiste() throws Exception {
        Long id = 99L;

        when(recetaService.obtenerRecetaPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/recetas/{id}", id))
                .andExpect(status().isNotFound());

        verify(recetaService, times(1)).obtenerRecetaPorId(id);
        verify(recetaAssembler, never()).toModel(any(Receta.class));
    }

    @Test
    @DisplayName("Debe crear receta")
    void shouldCrearReceta() throws Exception {
        Receta receta = crearReceta(1L);
        EntityModel<Receta> modelo = crearModelo(receta);
        String json = """
{
  "pacienteId": 1,
  "medicoId": 1,
  "medicamento": "Paracetamol",
  "dosis": "500 mg",
  "indicaciones": "Tomar cada 8 horas por 3 días",
  "fechaEmision": "2026-07-10"
}
                """;

        when(recetaService.guardarReceta(any(Receta.class))).thenReturn(receta);
        when(recetaAssembler.toModel(receta)).thenReturn(modelo);

        mockMvc.perform(post("/api/recetas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(recetaService, times(1)).guardarReceta(any(Receta.class));
        verify(recetaAssembler, times(1)).toModel(receta);
    }

    @Test
    @DisplayName("Debe eliminar receta existente")
    void shouldEliminarRecetaExistente() throws Exception {
        Long id = 1L;
        when(recetaService.eliminarReceta(id)).thenReturn(true);

        mockMvc.perform(delete("/api/recetas/{id}", id))
                .andExpect(status().isNoContent());

        verify(recetaService, times(1)).eliminarReceta(id);
    }

    @Test
    @DisplayName("Debe retornar 404 al eliminar receta inexistente")
    void shouldReturnNotFoundWhenEliminarRecetaNoExiste() throws Exception {
        Long id = 99L;
        when(recetaService.eliminarReceta(id)).thenReturn(false);

        mockMvc.perform(delete("/api/recetas/{id}", id))
                .andExpect(status().isNotFound());

        verify(recetaService, times(1)).eliminarReceta(id);
    }

    private EntityModel<Receta> crearModelo(Receta receta) {
        return EntityModel.of(
                receta,
                linkTo(methodOn(RecetaController.class).obtenerRecetaPorId(receta.getId())).withSelfRel()
        );
    }

    private Receta crearReceta(Long id) {
        Receta receta = new Receta();
        receta.setId(id);
        receta.setPacienteId(1L);
        receta.setMedicoId(1L);
        receta.setMedicamento("Paracetamol");
        receta.setDosis("500 mg");
        receta.setIndicaciones("Tomar cada 8 horas por 3 días");
        receta.setFechaEmision(String.valueOf("2026-07-10"));
        return receta;
    }
}
