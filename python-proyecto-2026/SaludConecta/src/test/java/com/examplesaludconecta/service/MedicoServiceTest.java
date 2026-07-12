package com.examplesaludconecta.service;

import com.examplesaludconecta.exception.ResourceNotFoundException;
import com.examplesaludconecta.model.Medico;
import com.examplesaludconecta.repository.MedicoRepository;
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
class MedicoServiceTest {

    @Mock
    private MedicoRepository medicoRepository;

    @InjectMocks
    private MedicoService medicoService;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker();
    }

    @Test
    @DisplayName("Debe obtener todos los médicos")
    void shouldObtenerTodosLosMedicos() {
        Medico medico1 = crearMedico(1L);
        Medico medico2 = crearMedico(2L);

        when(medicoRepository.findAll()).thenReturn(List.of(medico1, medico2));

        List<Medico> resultado = medicoService.obtenerTodosLosMedicos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        verify(medicoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe guardar un médico")
    void shouldGuardarMedico() {
        Medico medico = crearMedico(1L);

        when(medicoRepository.save(medico)).thenReturn(medico);

        Medico resultado = medicoService.guardarMedico(medico);

        assertNotNull(resultado);
        assertEquals(medico.getNombre(), resultado.getNombre());
        assertEquals(medico.getEmail(), resultado.getEmail());

        verify(medicoRepository, times(1)).save(medico);
    }

    @Test
    @DisplayName("Debe obtener un médico por ID")
    void shouldObtenerMedicoPorId() {
        Long id = 1L;
        Medico medico = crearMedico(id);

        when(medicoRepository.findById(id)).thenReturn(Optional.of(medico));

        Optional<Medico> resultado = medicoService.obtenerPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        assertEquals(medico.getNombre(), resultado.get().getNombre());

        verify(medicoRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe eliminar un médico existente")
    void shouldEliminarMedicoExistente() {
        Long id = 1L;

        when(medicoRepository.existsById(id)).thenReturn(true);

        medicoService.eliminarMedico(id);

        verify(medicoRepository, times(1)).existsById(id);
        verify(medicoRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar un médico inexistente")
    void shouldThrowExceptionWhenEliminarMedicoNoExiste() {
        Long id = 99L;

        when(medicoRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            medicoService.eliminarMedico(id);
        });

        verify(medicoRepository, times(1)).existsById(id);
        verify(medicoRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Debe actualizar un médico existente")
    void shouldActualizarMedicoExistente() {
        Long id = 1L;

        Medico medicoExistente = crearMedico(id);

        Medico medicoActualizado = new Medico();
        medicoActualizado.setNombre(faker.name().firstName());
        medicoActualizado.setApellido(faker.name().lastName());
        medicoActualizado.setRut("11.111.111-1");
        medicoActualizado.setEspecialidad("Cardiología");
        medicoActualizado.setEmail(faker.internet().emailAddress());

        when(medicoRepository.findById(id)).thenReturn(Optional.of(medicoExistente));
        when(medicoRepository.save(any(Medico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Medico resultado = medicoService.actualizarMedico(id, medicoActualizado);

        assertNotNull(resultado);
        assertEquals(medicoActualizado.getNombre(), resultado.getNombre());
        assertEquals(medicoActualizado.getApellido(), resultado.getApellido());
        assertEquals(medicoActualizado.getRut(), resultado.getRut());
        assertEquals(medicoActualizado.getEspecialidad(), resultado.getEspecialidad());
        assertEquals(medicoActualizado.getEmail(), resultado.getEmail());

        verify(medicoRepository, times(1)).findById(id);
        verify(medicoRepository, times(1)).save(medicoExistente);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar un médico inexistente")
    void shouldThrowExceptionWhenActualizarMedicoNoExiste() {
        Long id = 99L;
        Medico medicoActualizado = crearMedico(id);

        when(medicoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            medicoService.actualizarMedico(id, medicoActualizado);
        });

        verify(medicoRepository, times(1)).findById(id);
        verify(medicoRepository, never()).save(any(Medico.class));
    }

    private Medico crearMedico(Long id) {
        Medico medico = new Medico();

        medico.setId(id);
        medico.setNombre(faker.name().firstName());
        medico.setApellido(faker.name().lastName());
        medico.setRut("12.345.678-9");
        medico.setEspecialidad("Medicina General");
        medico.setEmail(faker.internet().emailAddress());

        return medico;
    }
}
