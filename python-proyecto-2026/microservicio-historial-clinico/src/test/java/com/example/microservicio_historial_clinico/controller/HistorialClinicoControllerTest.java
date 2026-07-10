package com.example.microservicio_historial_clinico.controller;

import com.example.microservicio_historial_clinico.assemblers.HistorialClinicoAssembler;
import com.example.microservicio_historial_clinico.model.HistorialClinico;
import com.example.microservicio_historial_clinico.service.HistorialClinicoService;
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

@WebMvcTest(HistorialClinicoController.class)
@AutoConfigureMockMvc(addFilters = false)
class HistorialClinicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HistorialClinicoService historialClinicoService;

    @MockitoBean
    private HistorialClinicoAssembler historialClinicoAssembler;

    @Test
    @DisplayName("Debe listar historiales-clinicos con enlaces HATEOAS")
    void shouldListarHistorialesClinicosConHateoas() throws Exception {
        HistorialClinico historialClinico = crearHistorialClinico(1L);
        EntityModel<HistorialClinico> modelo = crearModelo(historialClinico);

        when(historialClinicoService.listarHistorialesClinicos()).thenReturn(List.of(historialClinico));
        when(historialClinicoAssembler.toModel(historialClinico)).thenReturn(modelo);

        mockMvc.perform(get("/api/historiales-clinicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/historiales-clinicos")))
                .andExpect(jsonPath("$._embedded").exists());

        verify(historialClinicoService, times(1)).listarHistorialesClinicos();
        verify(historialClinicoAssembler, times(1)).toModel(historialClinico);
    }

    @Test
    @DisplayName("Debe obtener historialClinico por ID")
    void shouldObtenerHistorialClinicoPorId() throws Exception {
        Long id = 1L;
        HistorialClinico historialClinico = crearHistorialClinico(id);
        EntityModel<HistorialClinico> modelo = crearModelo(historialClinico);

        when(historialClinicoService.obtenerHistorialClinicoPorId(id)).thenReturn(Optional.of(historialClinico));
        when(historialClinicoAssembler.toModel(historialClinico)).thenReturn(modelo);

        mockMvc.perform(get("/api/historiales-clinicos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(historialClinicoService, times(1)).obtenerHistorialClinicoPorId(id);
        verify(historialClinicoAssembler, times(1)).toModel(historialClinico);
    }

    @Test
    @DisplayName("Debe retornar 404 si no existe")
    void shouldReturnNotFoundWhenHistorialClinicoNoExiste() throws Exception {
        Long id = 99L;

        when(historialClinicoService.obtenerHistorialClinicoPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/historiales-clinicos/{id}", id))
                .andExpect(status().isNotFound());

        verify(historialClinicoService, times(1)).obtenerHistorialClinicoPorId(id);
        verify(historialClinicoAssembler, never()).toModel(any(HistorialClinico.class));
    }

    @Test
    @DisplayName("Debe crear historialClinico")
    void shouldCrearHistorialClinico() throws Exception {
        HistorialClinico historialClinico = crearHistorialClinico(1L);
        EntityModel<HistorialClinico> modelo = crearModelo(historialClinico);
        String json = """
{
  "pacienteId": 1,
  "fechaRegistro": "2026-07-10",
  "antecedentes": "Hipertensión controlada",
  "observaciones": "Paciente estable",
  "alergias": "Penicilina"
}
                """;

        when(historialClinicoService.guardarHistorialClinico(any(HistorialClinico.class))).thenReturn(historialClinico);
        when(historialClinicoAssembler.toModel(historialClinico)).thenReturn(modelo);

        mockMvc.perform(post("/api/historiales-clinicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(historialClinicoService, times(1)).guardarHistorialClinico(any(HistorialClinico.class));
        verify(historialClinicoAssembler, times(1)).toModel(historialClinico);
    }

    @Test
    @DisplayName("Debe eliminar historialClinico existente")
    void shouldEliminarHistorialClinicoExistente() throws Exception {
        Long id = 1L;
        when(historialClinicoService.eliminarHistorialClinico(id)).thenReturn(true);

        mockMvc.perform(delete("/api/historiales-clinicos/{id}", id))
                .andExpect(status().isNoContent());

        verify(historialClinicoService, times(1)).eliminarHistorialClinico(id);
    }

    @Test
    @DisplayName("Debe retornar 404 al eliminar historialClinico inexistente")
    void shouldReturnNotFoundWhenEliminarHistorialClinicoNoExiste() throws Exception {
        Long id = 99L;
        when(historialClinicoService.eliminarHistorialClinico(id)).thenReturn(false);

        mockMvc.perform(delete("/api/historiales-clinicos/{id}", id))
                .andExpect(status().isNotFound());

        verify(historialClinicoService, times(1)).eliminarHistorialClinico(id);
    }

    private EntityModel<HistorialClinico> crearModelo(HistorialClinico historialClinico) {
        return EntityModel.of(
                historialClinico,
                linkTo(methodOn(HistorialClinicoController.class).obtenerHistorialClinicoPorId(historialClinico.getId())).withSelfRel()
        );
    }

    private HistorialClinico crearHistorialClinico(Long id) {
        HistorialClinico historialClinico = new HistorialClinico();
        historialClinico.setId(id);
        historialClinico.setPacienteId(1L);
        historialClinico.setFechaRegistro(String.valueOf("2026-07-10"));
        historialClinico.setAntecedentes("Hipertensión controlada");
        historialClinico.setObservaciones("Paciente estable");
        historialClinico.setAlergias("Penicilina");
        return historialClinico;
    }
}
