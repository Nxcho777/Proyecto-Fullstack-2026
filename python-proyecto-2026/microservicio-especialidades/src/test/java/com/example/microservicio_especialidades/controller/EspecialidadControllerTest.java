package com.example.microservicio_especialidades.controller;

import com.example.microservicio_especialidades.assemblers.EspecialidadAssembler;
import com.example.microservicio_especialidades.model.Especialidad;
import com.example.microservicio_especialidades.service.EspecialidadService;
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

@WebMvcTest(EspecialidadController.class)
@AutoConfigureMockMvc(addFilters = false)
class EspecialidadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EspecialidadService especialidadService;

    @MockitoBean
    private EspecialidadAssembler especialidadAssembler;

    @Test
    @DisplayName("Debe listar especialidades con enlaces HATEOAS")
    void shouldListarEspecialidadesConHateoas() throws Exception {
        Especialidad especialidad = crearEspecialidad(1L);
        EntityModel<Especialidad> modelo = crearModelo(especialidad);

        when(especialidadService.listarEspecialidades()).thenReturn(List.of(especialidad));
        when(especialidadAssembler.toModel(especialidad)).thenReturn(modelo);

        mockMvc.perform(get("/api/especialidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/especialidades")))
                .andExpect(jsonPath("$._embedded").exists());

        verify(especialidadService, times(1)).listarEspecialidades();
        verify(especialidadAssembler, times(1)).toModel(especialidad);
    }

    @Test
    @DisplayName("Debe obtener especialidad por ID")
    void shouldObtenerEspecialidadPorId() throws Exception {
        Long id = 1L;
        Especialidad especialidad = crearEspecialidad(id);
        EntityModel<Especialidad> modelo = crearModelo(especialidad);

        when(especialidadService.obtenerEspecialidadPorId(id)).thenReturn(Optional.of(especialidad));
        when(especialidadAssembler.toModel(especialidad)).thenReturn(modelo);

        mockMvc.perform(get("/api/especialidades/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(especialidadService, times(1)).obtenerEspecialidadPorId(id);
        verify(especialidadAssembler, times(1)).toModel(especialidad);
    }

    @Test
    @DisplayName("Debe retornar 404 si no existe")
    void shouldReturnNotFoundWhenEspecialidadNoExiste() throws Exception {
        Long id = 99L;

        when(especialidadService.obtenerEspecialidadPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/especialidades/{id}", id))
                .andExpect(status().isNotFound());

        verify(especialidadService, times(1)).obtenerEspecialidadPorId(id);
        verify(especialidadAssembler, never()).toModel(any(Especialidad.class));
    }

    @Test
    @DisplayName("Debe crear especialidad")
    void shouldCrearEspecialidad() throws Exception {
        Especialidad especialidad = crearEspecialidad(1L);
        EntityModel<Especialidad> modelo = crearModelo(especialidad);
        String json = """
{
  "nombre": "Cardiología",
  "descripcion": "Atención de enfermedades cardiovasculares",
  "area": "Medicina interna",
  "activa": true
}
                """;

        when(especialidadService.guardarEspecialidad(any(Especialidad.class))).thenReturn(especialidad);
        when(especialidadAssembler.toModel(especialidad)).thenReturn(modelo);

        mockMvc.perform(post("/api/especialidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(especialidadService, times(1)).guardarEspecialidad(any(Especialidad.class));
        verify(especialidadAssembler, times(1)).toModel(especialidad);
    }

    @Test
    @DisplayName("Debe eliminar especialidad existente")
    void shouldEliminarEspecialidadExistente() throws Exception {
        Long id = 1L;
        when(especialidadService.eliminarEspecialidad(id)).thenReturn(true);

        mockMvc.perform(delete("/api/especialidades/{id}", id))
                .andExpect(status().isNoContent());

        verify(especialidadService, times(1)).eliminarEspecialidad(id);
    }

    @Test
    @DisplayName("Debe retornar 404 al eliminar especialidad inexistente")
    void shouldReturnNotFoundWhenEliminarEspecialidadNoExiste() throws Exception {
        Long id = 99L;
        when(especialidadService.eliminarEspecialidad(id)).thenReturn(false);

        mockMvc.perform(delete("/api/especialidades/{id}", id))
                .andExpect(status().isNotFound());

        verify(especialidadService, times(1)).eliminarEspecialidad(id);
    }

    private EntityModel<Especialidad> crearModelo(Especialidad especialidad) {
        return EntityModel.of(
                especialidad,
                linkTo(methodOn(EspecialidadController.class).obtenerEspecialidadPorId(especialidad.getId())).withSelfRel()
        );
    }

    private Especialidad crearEspecialidad(Long id) {
        Especialidad especialidad = new Especialidad();
        especialidad.setId(id);
        especialidad.setNombre("Cardiología");
        especialidad.setDescripcion("Atención de enfermedades cardiovasculares");
        especialidad.setArea("Medicina interna");
        especialidad.setActiva(true);
        return especialidad;
    }
}
