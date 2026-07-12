package com.examplesaludconecta.service;

import com.examplesaludconecta.exception.ResourceNotFoundException;
import com.examplesaludconecta.model.Tratamiento;
import com.examplesaludconecta.repository.TratamientoRepository;
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
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TratamientoServiceTest {

    @Mock
    private TratamientoRepository tratamientoRepository;

    @InjectMocks
    private TratamientoService tratamientoService;

    private Faker faker;
    private Random random;

    @BeforeEach
    void setUp() {
        faker = new Faker();
        random = new Random();
    }

    @Test
    @DisplayName("Debe obtener todos los tratamientos")
    void shouldObtenerTodosLosTratamientos() {
        Tratamiento tratamiento1 = crearTratamiento(1L);
        Tratamiento tratamiento2 = crearTratamiento(2L);

        when(tratamientoRepository.findAll()).thenReturn(List.of(tratamiento1, tratamiento2));

        List<Tratamiento> resultado = tratamientoService.obtenerTodosLosTratamientos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        verify(tratamientoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe guardar un tratamiento")
    void shouldGuardarTratamiento() {
        Tratamiento tratamiento = crearTratamiento(1L);

        when(tratamientoRepository.save(tratamiento)).thenReturn(tratamiento);

        Tratamiento resultado = tratamientoService.guardarTratamiento(tratamiento);

        assertNotNull(resultado);
        assertEquals(tratamiento.getDiagnostico(), resultado.getDiagnostico());
        assertEquals(tratamiento.getMedicamento(), resultado.getMedicamento());

        verify(tratamientoRepository, times(1)).save(tratamiento);
    }

    @Test
    @DisplayName("Debe obtener un tratamiento por ID")
    void shouldObtenerTratamientoPorId() {
        Long id = 1L;
        Tratamiento tratamiento = crearTratamiento(id);

        when(tratamientoRepository.findById(id)).thenReturn(Optional.of(tratamiento));

        Optional<Tratamiento> resultado = tratamientoService.obtenerPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        assertEquals(tratamiento.getDiagnostico(), resultado.get().getDiagnostico());

        verify(tratamientoRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe eliminar un tratamiento existente")
    void shouldEliminarTratamientoExistente() {
        Long id = 1L;

        when(tratamientoRepository.existsById(id)).thenReturn(true);

        tratamientoService.eliminarTratamiento(id);

        verify(tratamientoRepository, times(1)).existsById(id);
        verify(tratamientoRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar un tratamiento inexistente")
    void shouldThrowExceptionWhenEliminarTratamientoNoExiste() {
        Long id = 99L;

        when(tratamientoRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            tratamientoService.eliminarTratamiento(id);
        });

        verify(tratamientoRepository, times(1)).existsById(id);
        verify(tratamientoRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Debe actualizar un tratamiento existente")
    void shouldActualizarTratamientoExistente() {
        Long id = 1L;

        Tratamiento tratamientoExistente = crearTratamiento(id);

        Tratamiento tratamientoActualizado = new Tratamiento();
        tratamientoActualizado.setDiagnostico("Gripe");
        tratamientoActualizado.setMedicamento("Paracetamol");
        tratamientoActualizado.setDosis("1 comprimido cada 8 horas");
        tratamientoActualizado.setDuracionDias(7);

        when(tratamientoRepository.findById(id)).thenReturn(Optional.of(tratamientoExistente));
        when(tratamientoRepository.save(any(Tratamiento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tratamiento resultado = tratamientoService.actualizarTratamiento(id, tratamientoActualizado);

        assertNotNull(resultado);
        assertEquals(tratamientoActualizado.getDiagnostico(), resultado.getDiagnostico());
        assertEquals(tratamientoActualizado.getMedicamento(), resultado.getMedicamento());
        assertEquals(tratamientoActualizado.getDosis(), resultado.getDosis());
        assertEquals(tratamientoActualizado.getDuracionDias(), resultado.getDuracionDias());

        verify(tratamientoRepository, times(1)).findById(id);
        verify(tratamientoRepository, times(1)).save(tratamientoExistente);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar un tratamiento inexistente")
    void shouldThrowExceptionWhenActualizarTratamientoNoExiste() {
        Long id = 99L;
        Tratamiento tratamientoActualizado = crearTratamiento(id);

        when(tratamientoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            tratamientoService.actualizarTratamiento(id, tratamientoActualizado);
        });

        verify(tratamientoRepository, times(1)).findById(id);
        verify(tratamientoRepository, never()).save(any(Tratamiento.class));
    }

    private Tratamiento crearTratamiento(Long id) {
        String[] diagnosticos = {
                "Gripe",
                "Bronquitis",
                "Asma",
                "Migraña",
                "Hipertensión"
        };

        String[] medicamentos = {
                "Paracetamol",
                "Ibuprofeno",
                "Amoxicilina",
                "Loratadina",
                "Omeprazol"
        };

        String[] dosis = {
                "1 comprimido cada 8 horas",
                "1 comprimido cada 12 horas",
                "5 ml cada 8 horas",
                "1 cápsula diaria"
        };

        Tratamiento tratamiento = new Tratamiento();

        tratamiento.setId(id);
        tratamiento.setDiagnostico(diagnosticos[random.nextInt(diagnosticos.length)]);
        tratamiento.setMedicamento(medicamentos[random.nextInt(medicamentos.length)]);
        tratamiento.setDosis(dosis[random.nextInt(dosis.length)]);
        tratamiento.setDuracionDias(faker.number().numberBetween(1, 30));

        return tratamiento;
    }
}
