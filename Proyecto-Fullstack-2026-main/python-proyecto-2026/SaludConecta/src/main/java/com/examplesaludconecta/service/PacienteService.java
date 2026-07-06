package com.examplesaludconecta.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.examplesaludconecta.exception.ResourceNotFoundException;
import com.examplesaludconecta.model.Paciente;
import com.examplesaludconecta.repository.PacienteRepository;

@Service
@SuppressWarnings("null") 
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    public List<Paciente> obtenerTodosLosPacientes() {
        log.info("Listando todos los pacientes encontrados");
        return pacienteRepository.findAll();
    }

    public Paciente guardarPaciente(Paciente paciente) {
        log.info("Guardando paciente: {}", paciente.getNombre());
        return pacienteRepository.save(paciente);
    }
    private static final Logger log = 
            LoggerFactory.getLogger(PacienteService.class);

    public Optional<Paciente> obtenerPorId(Long id) {
        return pacienteRepository.findById(id);
    }

    public void eliminarPaciente(Long id) {
    if (!pacienteRepository.existsById(id)) {
        log.warn("Esta intentando eliminar un paciente con un id inexistente {}", id);
        throw new ResourceNotFoundException("Paciente no encontrado con este id: " + id);
    }
        log.info("Eliminando paciente con id {}", id);
        pacienteRepository.deleteById(id);
    }

    public Paciente actualizarPaciente(Long id, Paciente actualizado) {
        log.info("Actualizando paciente con id: {}", id);
        return pacienteRepository.findById(id).map(paciente -> {
            paciente.setNombre(actualizado.getNombre());
            paciente.setApellido(actualizado.getApellido());
            paciente.setRut(actualizado.getRut());
            return pacienteRepository.save(paciente);
        }).orElseThrow(() -> {
    log.warn("Esta intentando actualizar un paciente con un id inexistente {}", id);
        return new ResourceNotFoundException("Paciente no encontrado con este id: " + id);
});
    }
}