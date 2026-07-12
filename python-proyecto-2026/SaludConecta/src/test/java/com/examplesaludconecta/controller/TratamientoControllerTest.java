package com.examplesaludconecta.controller;

import com.examplesaludconecta.exception.ResourceNotFoundException;
import com.examplesaludconecta.model.Tratamiento;
import com.examplesaludconecta.service.TratamientoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TratamientoController.class)
class TratamientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TratamientoService tratamientoService;

    @Test
    @DisplayName("Debe listar tratamientos con enlaces HATEOAS")
    void debeListarTratamientos() throws Exception {
        Tratamiento tratamiento = crearTratamiento(1L);
        when(tratamientoService.obtenerTodosLosTratamientos()).thenReturn(List.of(tratamiento));

        mockMvc.perform(get("/api/tratamientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/tratamientos")));

        verify(tratamientoService).obtenerTodosLosTratamientos();
    }

    @Test
    @DisplayName("Debe obtener un tratamiento por ID")
    void debeObtenerTratamientoPorId() throws Exception {
        Tratamiento tratamiento = crearTratamiento(1L);
        when(tratamientoService.obtenerPorId(1L)).thenReturn(Optional.of(tratamiento));

        mockMvc.perform(get("/api/tratamientos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.diagnostico").value("Hipertensión"))
                .andExpect(jsonPath("$.duracionDias").value(30))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("Debe retornar 404 si el tratamiento no existe")
    void debeRetornar404CuandoTratamientoNoExiste() throws Exception {
        when(tratamientoService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tratamientos/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.mensaje").value("Tratamiento no encontrado con id: 99"));
    }

    @Test
    @DisplayName("Debe crear un tratamiento válido")
    void debeCrearTratamiento() throws Exception {
        Tratamiento tratamiento = crearTratamiento(1L);
        when(tratamientoService.guardarTratamiento(any(Tratamiento.class))).thenReturn(tratamiento);

        mockMvc.perform(post("/api/tratamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTratamientoValido()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.medicamento").value("Losartán"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("Debe retornar 400 al crear un tratamiento con datos inválidos")
    void debeRetornar400AlCrearTratamientoInvalido() throws Exception {
        String jsonInvalido = """
                {
                    "diagnostico": "",
                    "medicamento": "",
                    "dosis": "",
                    "duracionDias": 0
                }
                """;

        mockMvc.perform(post("/api/tratamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.detalles.diagnostico").exists())
                .andExpect(jsonPath("$.detalles.medicamento").exists())
                .andExpect(jsonPath("$.detalles.duracionDias").exists());

        verify(tratamientoService, never()).guardarTratamiento(any(Tratamiento.class));
    }

    @Test
    @DisplayName("Debe actualizar un tratamiento")
    void debeActualizarTratamiento() throws Exception {
        Tratamiento tratamiento = crearTratamiento(1L);
        tratamiento.setDuracionDias(60);
        when(tratamientoService.actualizarTratamiento(any(Long.class), any(Tratamiento.class)))
                .thenReturn(tratamiento);

        String jsonActualizado = """
                {
                    "diagnostico": "Hipertensión",
                    "medicamento": "Losartán",
                    "dosis": "50 mg cada 12 horas",
                    "duracionDias": 60
                }
                """;

        mockMvc.perform(put("/api/tratamientos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonActualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duracionDias").value(60));
    }

    @Test
    @DisplayName("Debe eliminar un tratamiento existente")
    void debeEliminarTratamiento() throws Exception {
        mockMvc.perform(delete("/api/tratamientos/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(tratamientoService).eliminarTratamiento(1L);
    }

    @Test
    @DisplayName("Debe retornar 404 al eliminar un tratamiento inexistente")
    void debeRetornar404AlEliminarTratamientoInexistente() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Tratamiento no encontrado con id: 99"))
                .when(tratamientoService).eliminarTratamiento(99L);

        mockMvc.perform(delete("/api/tratamientos/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404));
    }

    private Tratamiento crearTratamiento(Long id) {
        return new Tratamiento(
                id,
                "Hipertensión",
                "Losartán",
                "50 mg cada 12 horas",
                30
        );
    }

    private String jsonTratamientoValido() {
        return """
                {
                    "diagnostico": "Hipertensión",
                    "medicamento": "Losartán",
                    "dosis": "50 mg cada 12 horas",
                    "duracionDias": 30
                }
                """;
    }
}
