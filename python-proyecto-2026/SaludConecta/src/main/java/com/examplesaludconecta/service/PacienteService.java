package com.examplesaludconecta.service;

import com.examplesaludconecta.exception.ReglaNegocioException;
import com.examplesaludconecta.exception.ResourceNotFoundException;
import com.examplesaludconecta.model.Paciente;
import com.examplesaludconecta.repository.PacienteRepository;
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
public class PacienteService {

    private static final Logger log = LoggerFactory.getLogger(PacienteService.class);

    @Autowired
    private PacienteRepository pacienteRepository;

    public List<Paciente> obtenerTodosLosPacientes() {
        log.info("Listando todos los pacientes encontrados");
        return pacienteRepository.findAll();
    }

    public Paciente guardarPaciente(@Valid Paciente paciente) {
        validarDatosUnicos(paciente);
        log.info("Guardando paciente: {}", paciente.getNombre());
        return pacienteRepository.save(paciente);
    }

    public Optional<Paciente> obtenerPorId(Long id) {
        log.info("Buscando paciente con id {}", id);
        return pacienteRepository.findById(id);
    }

    public void eliminarPaciente(Long id) {
        if (!pacienteRepository.existsById(id)) {
            log.warn("Está intentando eliminar un paciente con un id inexistente {}", id);
            throw new ResourceNotFoundException("Paciente no encontrado con este id: " + id);
        }

        log.info("Eliminando paciente con id {}", id);
        pacienteRepository.deleteById(id);
    }

    public Paciente actualizarPaciente(Long id, @Valid Paciente actualizado) {
        log.info("Actualizando paciente con id: {}", id);

        return pacienteRepository.findById(id).map(paciente -> {
            validarDatosUnicosEnActualizacion(id, actualizado);
            paciente.setNombre(actualizado.getNombre());
            paciente.setApellido(actualizado.getApellido());
            paciente.setRut(actualizado.getRut());
            paciente.setCorreo(actualizado.getCorreo());
            paciente.setTelefono(actualizado.getTelefono());
            return pacienteRepository.save(paciente);
        }).orElseThrow(() -> {
            log.warn("Está intentando actualizar un paciente con un id inexistente {}", id);
            return new ResourceNotFoundException("Paciente no encontrado con este id: " + id);
        });
    }

    private void validarDatosUnicos(Paciente paciente) {
        if (pacienteRepository.existsByRut(paciente.getRut())) {
            throw new ReglaNegocioException("Ya existe un paciente registrado con el RUT ingresado");
        }

        if (pacienteRepository.existsByCorreo(paciente.getCorreo())) {
            throw new ReglaNegocioException("Ya existe un paciente registrado con el correo ingresado");
        }
    }

    private void validarDatosUnicosEnActualizacion(Long id, Paciente actualizado) {
        if (pacienteRepository.existsByRutAndIdNot(actualizado.getRut(), id)) {
            throw new ReglaNegocioException("El RUT ingresado pertenece a otro paciente");
        }

        if (pacienteRepository.existsByCorreoAndIdNot(actualizado.getCorreo(), id)) {
            throw new ReglaNegocioException("El correo ingresado pertenece a otro paciente");
        }
    }
}
