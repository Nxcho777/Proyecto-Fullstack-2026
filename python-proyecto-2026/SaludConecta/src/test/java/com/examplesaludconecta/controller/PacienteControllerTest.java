package com.examplesaludconecta.controller;

import com.examplesaludconecta.exception.ResourceNotFoundException;
import com.examplesaludconecta.model.Paciente;
import com.examplesaludconecta.service.PacienteService;
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

@WebMvcTest(PacienteController.class)
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PacienteService pacienteService;

    @Test
    @DisplayName("Debe listar pacientes con enlaces HATEOAS")
    void debeListarPacientes() throws Exception {
        Paciente paciente = crearPaciente(1L);
        when(pacienteService.obtenerTodosLosPacientes()).thenReturn(List.of(paciente));

        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/pacientes")));

        verify(pacienteService).obtenerTodosLosPacientes();
    }

    @Test
    @DisplayName("Debe obtener un paciente por ID")
    void debeObtenerPacientePorId() throws Exception {
        Paciente paciente = crearPaciente(1L);
        when(pacienteService.obtenerPorId(1L)).thenReturn(Optional.of(paciente));

        mockMvc.perform(get("/api/pacientes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.correo").value("carlos@gmail.com"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("Debe retornar 404 si el paciente no existe")
    void debeRetornar404CuandoPacienteNoExiste() throws Exception {
        when(pacienteService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pacientes/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.mensaje").value("Paciente no encontrado con id: 99"));
    }

    @Test
    @DisplayName("Debe crear un paciente válido")
    void debeCrearPaciente() throws Exception {
        Paciente paciente = crearPaciente(1L);
        when(pacienteService.guardarPaciente(any(Paciente.class))).thenReturn(paciente);

        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPacienteValido()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rut").value("13.456.789-K"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("Debe retornar 400 al crear un paciente con datos inválidos")
    void debeRetornar400AlCrearPacienteInvalido() throws Exception {
        String jsonInvalido = """
                {
                    "nombre": "",
                    "apellido": "",
                    "rut": "123",
                    "correo": "correo-invalido",
                    "telefono": "12"
                }
                """;

        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.detalles.nombre").exists())
                .andExpect(jsonPath("$.detalles.rut").exists())
                .andExpect(jsonPath("$.detalles.correo").exists())
                .andExpect(jsonPath("$.detalles.telefono").exists());

        verify(pacienteService, never()).guardarPaciente(any(Paciente.class));
    }

    @Test
    @DisplayName("Debe actualizar un paciente")
    void debeActualizarPaciente() throws Exception {
        Paciente paciente = crearPaciente(1L);
        paciente.setTelefono("+56999999999");
        when(pacienteService.actualizarPaciente(any(Long.class), any(Paciente.class)))
                .thenReturn(paciente);

        String jsonActualizado = """
                {
                    "nombre": "Carlos",
                    "apellido": "Soto",
                    "rut": "13.456.789-K",
                    "correo": "carlos@gmail.com",
                    "telefono": "+56999999999"
                }
                """;

        mockMvc.perform(put("/api/pacientes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonActualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telefono").value("+56999999999"));
    }

    @Test
    @DisplayName("Debe eliminar un paciente existente")
    void debeEliminarPaciente() throws Exception {
        mockMvc.perform(delete("/api/pacientes/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(pacienteService).eliminarPaciente(1L);
    }

    @Test
    @DisplayName("Debe retornar 404 al eliminar un paciente inexistente")
    void debeRetornar404AlEliminarPacienteInexistente() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Paciente no encontrado con este id: 99"))
                .when(pacienteService).eliminarPaciente(99L);

        mockMvc.perform(delete("/api/pacientes/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404));
    }

    private Paciente crearPaciente(Long id) {
        return new Paciente(
                id,
                "Carlos",
                "Soto",
                "13.456.789-K",
                "carlos@gmail.com",
                "+56912345678"
        );
    }

    private String jsonPacienteValido() {
        return """
                {
                    "nombre": "Carlos",
                    "apellido": "Soto",
                    "rut": "13.456.789-K",
                    "correo": "carlos@gmail.com",
                    "telefono": "+56912345678"
                }
                """;
    }
}
