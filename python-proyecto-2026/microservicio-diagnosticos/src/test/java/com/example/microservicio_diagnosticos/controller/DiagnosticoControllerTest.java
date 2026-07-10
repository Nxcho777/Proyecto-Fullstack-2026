package com.example.microservicio_diagnosticos.controller;

import com.example.microservicio_diagnosticos.assemblers.DiagnosticoAssembler;
import com.example.microservicio_diagnosticos.model.Diagnostico;
import com.example.microservicio_diagnosticos.service.DiagnosticoService;
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

@WebMvcTest(DiagnosticoController.class)
@AutoConfigureMockMvc(addFilters = false)
class DiagnosticoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiagnosticoService diagnosticoService;

    @MockitoBean
    private DiagnosticoAssembler diagnosticoAssembler;

    @Test
    @DisplayName("Debe listar diagnosticos con enlaces HATEOAS")
    void shouldListarDiagnosticosConHateoas() throws Exception {
        Diagnostico diagnostico = crearDiagnostico(1L);
        EntityModel<Diagnostico> modelo = crearModelo(diagnostico);

        when(diagnosticoService.listarDiagnosticos()).thenReturn(List.of(diagnostico));
        when(diagnosticoAssembler.toModel(diagnostico)).thenReturn(modelo);

        mockMvc.perform(get("/api/diagnosticos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/diagnosticos")))
                .andExpect(jsonPath("$._embedded").exists());

        verify(diagnosticoService, times(1)).listarDiagnosticos();
        verify(diagnosticoAssembler, times(1)).toModel(diagnostico);
    }

    @Test
    @DisplayName("Debe obtener diagnostico por ID")
    void shouldObtenerDiagnosticoPorId() throws Exception {
        Long id = 1L;
        Diagnostico diagnostico = crearDiagnostico(id);
        EntityModel<Diagnostico> modelo = crearModelo(diagnostico);

        when(diagnosticoService.obtenerDiagnosticoPorId(id)).thenReturn(Optional.of(diagnostico));
        when(diagnosticoAssembler.toModel(diagnostico)).thenReturn(modelo);

        mockMvc.perform(get("/api/diagnosticos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(diagnosticoService, times(1)).obtenerDiagnosticoPorId(id);
        verify(diagnosticoAssembler, times(1)).toModel(diagnostico);
    }

    @Test
    @DisplayName("Debe retornar 404 si no existe")
    void shouldReturnNotFoundWhenDiagnosticoNoExiste() throws Exception {
        Long id = 99L;

        when(diagnosticoService.obtenerDiagnosticoPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/diagnosticos/{id}", id))
                .andExpect(status().isNotFound());

        verify(diagnosticoService, times(1)).obtenerDiagnosticoPorId(id);
        verify(diagnosticoAssembler, never()).toModel(any(Diagnostico.class));
    }

    @Test
    @DisplayName("Debe crear diagnostico")
    void shouldCrearDiagnostico() throws Exception {
        Diagnostico diagnostico = crearDiagnostico(1L);
        EntityModel<Diagnostico> modelo = crearModelo(diagnostico);
        String json = """
{
  "pacienteId": 1,
  "medicoId": 1,
  "descripcion": "Gripe común",
  "gravedad": "LEVE",
  "fecha": "2026-07-10"
}
                """;

        when(diagnosticoService.guardarDiagnostico(any(Diagnostico.class))).thenReturn(diagnostico);
        when(diagnosticoAssembler.toModel(diagnostico)).thenReturn(modelo);

        mockMvc.perform(post("/api/diagnosticos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(diagnosticoService, times(1)).guardarDiagnostico(any(Diagnostico.class));
        verify(diagnosticoAssembler, times(1)).toModel(diagnostico);
    }

    @Test
    @DisplayName("Debe eliminar diagnostico existente")
    void shouldEliminarDiagnosticoExistente() throws Exception {
        Long id = 1L;
        when(diagnosticoService.eliminarDiagnostico(id)).thenReturn(true);

        mockMvc.perform(delete("/api/diagnosticos/{id}", id))
                .andExpect(status().isNoContent());

        verify(diagnosticoService, times(1)).eliminarDiagnostico(id);
    }

    @Test
    @DisplayName("Debe retornar 404 al eliminar diagnostico inexistente")
    void shouldReturnNotFoundWhenEliminarDiagnosticoNoExiste() throws Exception {
        Long id = 99L;
        when(diagnosticoService.eliminarDiagnostico(id)).thenReturn(false);

        mockMvc.perform(delete("/api/diagnosticos/{id}", id))
                .andExpect(status().isNotFound());

        verify(diagnosticoService, times(1)).eliminarDiagnostico(id);
    }

    private EntityModel<Diagnostico> crearModelo(Diagnostico diagnostico) {
        return EntityModel.of(
                diagnostico,
                linkTo(methodOn(DiagnosticoController.class).obtenerDiagnosticoPorId(diagnostico.getId())).withSelfRel()
        );
    }

    private Diagnostico crearDiagnostico(Long id) {
        Diagnostico diagnostico = new Diagnostico();
        diagnostico.setId(id);
        diagnostico.setPacienteId(1L);
        diagnostico.setMedicoId(1L);
        diagnostico.setDescripcion("Gripe común");
        diagnostico.setGravedad("LEVE");
        diagnostico.setFecha("2026-07-10");
        return diagnostico;
    }
}
