package com.examplesaludconecta.service;

import com.examplesaludconecta.exception.ResourceNotFoundException;
import com.examplesaludconecta.model.Tratamiento;
import com.examplesaludconecta.repository.TratamientoRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
@SuppressWarnings("null")
public class TratamientoService {

    private static final Logger log = LoggerFactory.getLogger(TratamientoService.class);

    @Autowired
    private TratamientoRepository tratamientoRepository;

    public List<Tratamiento> obtenerTodosLosTratamientos() {
        log.info("Listando todos los tratamientos encontrados");
        return tratamientoRepository.findAll();
    }

    public Tratamiento guardarTratamiento(@Valid Tratamiento tratamiento) {
        log.info("Guardando tratamiento con un diagnóstico asignado {}", tratamiento.getDiagnostico());
        return tratamientoRepository.save(tratamiento);
    }

    public Optional<Tratamiento> obtenerPorId(Long id) {
        log.info("Buscando tratamiento con su id correspondiente {}", id);
        return tratamientoRepository.findById(id);
    }

    public void eliminarTratamiento(Long id) {
        if (!tratamientoRepository.existsById(id)) {
            log.warn("El intento de eliminar un tratamiento inexistente ha sido fallido {}", id);
            throw new ResourceNotFoundException("Tratamiento no encontrado con id: " + id);
        }

        log.info("Eliminación del tratamiento con id {}", id);
        tratamientoRepository.deleteById(id);
    }

    public Tratamiento actualizarTratamiento(Long id, @Valid Tratamiento actualizado) {
        log.info("Actualizando tratamiento con id {}", id);

        return tratamientoRepository.findById(id).map(tratamiento -> {
            tratamiento.setDiagnostico(actualizado.getDiagnostico());
            tratamiento.setMedicamento(actualizado.getMedicamento());
            tratamiento.setDosis(actualizado.getDosis());
            tratamiento.setDuracionDias(actualizado.getDuracionDias());
            return tratamientoRepository.save(tratamiento);
        }).orElseThrow(() -> {
            log.warn("El intento de actualizar un tratamiento inexistente ha sido fallido {}", id);
            return new ResourceNotFoundException("Tratamiento no encontrado con id: " + id);
        });
    }
}
