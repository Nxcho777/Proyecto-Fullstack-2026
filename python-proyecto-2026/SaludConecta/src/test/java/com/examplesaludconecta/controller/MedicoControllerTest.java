package com.examplesaludconecta.controller;

import com.examplesaludconecta.exception.ResourceNotFoundException;
import com.examplesaludconecta.model.Medico;
import com.examplesaludconecta.service.MedicoService;
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

@WebMvcTest(MedicoController.class)
class MedicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedicoService medicoService;

    @Test
    @DisplayName("Debe listar médicos con enlaces HATEOAS")
    void debeListarMedicos() throws Exception {
        Medico medico = crearMedico(1L);
        when(medicoService.obtenerTodosLosMedicos()).thenReturn(List.of(medico));

        mockMvc.perform(get("/api/medicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/medicos")));

        verify(medicoService).obtenerTodosLosMedicos();
    }

    @Test
    @DisplayName("Debe obtener un médico por ID")
    void debeObtenerMedicoPorId() throws Exception {
        Medico medico = crearMedico(1L);
        when(medicoService.obtenerPorId(1L)).thenReturn(Optional.of(medico));

        mockMvc.perform(get("/api/medicos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("María"))
                .andExpect(jsonPath("$.email").value("maria@saludconecta.cl"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("Debe retornar 404 si el médico no existe")
    void debeRetornar404CuandoMedicoNoExiste() throws Exception {
        when(medicoService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/medicos/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.mensaje").value("Médico no encontrado con id: 99"));
    }

    @Test
    @DisplayName("Debe crear un médico válido")
    void debeCrearMedico() throws Exception {
        Medico medico = crearMedico(1L);
        when(medicoService.guardarMedico(any(Medico.class))).thenReturn(medico);

        mockMvc.perform(post("/api/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMedicoValido()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rut").value("12.345.678-9"))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(medicoService).guardarMedico(any(Medico.class));
    }

    @Test
    @DisplayName("Debe retornar 400 al crear un médico con datos inválidos")
    void debeRetornar400AlCrearMedicoInvalido() throws Exception {
        String jsonInvalido = """
                {
                    "nombre": "",
                    "apellido": "",
                    "rut": "rut-invalido",
                    "especialidad": "",
                    "email": "correo-invalido"
                }
                """;

        mockMvc.perform(post("/api/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.detalles.nombre").exists())
                .andExpect(jsonPath("$.detalles.rut").exists())
                .andExpect(jsonPath("$.detalles.email").exists());

        verify(medicoService, never()).guardarMedico(any(Medico.class));
    }

    @Test
    @DisplayName("Debe actualizar un médico")
    void debeActualizarMedico() throws Exception {
        Medico medico = crearMedico(1L);
        medico.setEspecialidad("Neurología");
        when(medicoService.actualizarMedico(any(Long.class), any(Medico.class)))
                .thenReturn(medico);

        String jsonActualizado = """
                {
                    "nombre": "María",
                    "apellido": "González",
                    "rut": "12.345.678-9",
                    "especialidad": "Neurología",
                    "email": "maria@saludconecta.cl"
                }
                """;

        mockMvc.perform(put("/api/medicos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonActualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.especialidad").value("Neurología"));
    }

    @Test
    @DisplayName("Debe eliminar un médico existente")
    void debeEliminarMedico() throws Exception {
        mockMvc.perform(delete("/api/medicos/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(medicoService).eliminarMedico(1L);
    }

    @Test
    @DisplayName("Debe retornar 404 al eliminar un médico inexistente")
    void debeRetornar404AlEliminarMedicoInexistente() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Médico no encontrado con id: 99"))
                .when(medicoService).eliminarMedico(99L);

        mockMvc.perform(delete("/api/medicos/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404));
    }

    private Medico crearMedico(Long id) {
        return new Medico(
                id,
                "María",
                "González",
                "12.345.678-9",
                "Cardiología",
                "maria@saludconecta.cl"
        );
    }

    private String jsonMedicoValido() {
        return """
                {
                    "nombre": "María",
                    "apellido": "González",
                    "rut": "12.345.678-9",
                    "especialidad": "Cardiología",
                    "email": "maria@saludconecta.cl"
                }
                """;
    }
}
