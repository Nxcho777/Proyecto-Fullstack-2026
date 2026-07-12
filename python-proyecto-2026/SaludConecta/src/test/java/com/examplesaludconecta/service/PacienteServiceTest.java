package com.examplesaludconecta.service;

import com.examplesaludconecta.exception.ResourceNotFoundException;
import com.examplesaludconecta.model.Paciente;
import com.examplesaludconecta.repository.PacienteRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteService pacienteService;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker();
    }

    @Test
    @DisplayName("Debe obtener todos los pacientes")
    void shouldObtenerTodosLosPacientes() {
        Paciente paciente1 = crearPaciente(1L);
        Paciente paciente2 = crearPaciente(2L);

        when(pacienteRepository.findAll()).thenReturn(List.of(paciente1, paciente2));

        List<Paciente> resultado = pacienteService.obtenerTodosLosPacientes();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        verify(pacienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe guardar un paciente")
    void shouldGuardarPaciente() {
        Paciente paciente = crearPaciente(1L);

        when(pacienteRepository.save(paciente)).thenReturn(paciente);

        Paciente resultado = pacienteService.guardarPaciente(paciente);

        assertNotNull(resultado);
        assertEquals(paciente.getNombre(), resultado.getNombre());
        assertEquals(paciente.getCorreo(), resultado.getCorreo());

        verify(pacienteRepository, times(1)).save(paciente);
    }

    @Test
    @DisplayName("Debe obtener un paciente por ID")
    void shouldObtenerPacientePorId() {
        Long id = 1L;
        Paciente paciente = crearPaciente(id);

        when(pacienteRepository.findById(id)).thenReturn(Optional.of(paciente));

        Optional<Paciente> resultado = pacienteService.obtenerPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        assertEquals(paciente.getNombre(), resultado.get().getNombre());

        verify(pacienteRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe eliminar un paciente existente")
    void shouldEliminarPacienteExistente() {
        Long id = 1L;

        when(pacienteRepository.existsById(id)).thenReturn(true);

        pacienteService.eliminarPaciente(id);

        verify(pacienteRepository, times(1)).existsById(id);
        verify(pacienteRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar un paciente inexistente")
    void shouldThrowExceptionWhenEliminarPacienteNoExiste() {
        Long id = 99L;

        when(pacienteRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            pacienteService.eliminarPaciente(id);
        });

        verify(pacienteRepository, times(1)).existsById(id);
        verify(pacienteRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Debe actualizar un paciente existente")
    void shouldActualizarPacienteExistente() {
        Long id = 1L;

        Paciente pacienteExistente = crearPaciente(id);

        Paciente pacienteActualizado = new Paciente();
        pacienteActualizado.setNombre(faker.name().firstName());
        pacienteActualizado.setApellido(faker.name().lastName());
        pacienteActualizado.setRut("11.111.111-1");

        when(pacienteRepository.findById(id)).thenReturn(Optional.of(pacienteExistente));
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Paciente resultado = pacienteService.actualizarPaciente(id, pacienteActualizado);

        assertNotNull(resultado);
        assertEquals(pacienteActualizado.getNombre(), resultado.getNombre());
        assertEquals(pacienteActualizado.getApellido(), resultado.getApellido());
        assertEquals(pacienteActualizado.getRut(), resultado.getRut());

        verify(pacienteRepository, times(1)).findById(id);
        verify(pacienteRepository, times(1)).save(pacienteExistente);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar un paciente inexistente")
    void shouldThrowExceptionWhenActualizarPacienteNoExiste() {
        Long id = 99L;
        Paciente pacienteActualizado = crearPaciente(id);

        when(pacienteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            pacienteService.actualizarPaciente(id, pacienteActualizado);
        });

        verify(pacienteRepository, times(1)).findById(id);
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    private Paciente crearPaciente(Long id) {
        Paciente paciente = new Paciente();

        paciente.setId(id);
        paciente.setNombre(faker.name().firstName());
        paciente.setApellido(faker.name().lastName());
        paciente.setRut("12.345.678-9");
        paciente.setCorreo(faker.internet().emailAddress());
        paciente.setTelefono("+569" + faker.number().digits(8));

        return paciente;
    }
}
