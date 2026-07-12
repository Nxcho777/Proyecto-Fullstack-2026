package com.examplesaludconecta.service;

import com.examplesaludconecta.exception.ReglaNegocioException;
import com.examplesaludconecta.exception.ResourceNotFoundException;
import com.examplesaludconecta.model.Medico;
import com.examplesaludconecta.repository.MedicoRepository;
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
public class MedicoService {

    private static final Logger log = LoggerFactory.getLogger(MedicoService.class);

    @Autowired
    private MedicoRepository medicoRepository;

    public List<Medico> obtenerTodosLosMedicos() {
        log.info("Listando todos los médicos encontrados");
        return medicoRepository.findAll();
    }

    public Medico guardarMedico(@Valid Medico medico) {
        validarDatosUnicos(medico);
        log.info("Guardando médico con RUT {}", medico.getRut());
        return medicoRepository.save(medico);
    }

    public Optional<Medico> obtenerPorId(Long id) {
        log.info("Buscando médico con id {}", id);
        return medicoRepository.findById(id);
    }

    public void eliminarMedico(Long id) {
        if (!medicoRepository.existsById(id)) {
            log.warn("Se intentó eliminar un médico inexistente con id {}", id);
            throw new ResourceNotFoundException("Médico no encontrado con id: " + id);
        }

        log.info("Eliminando médico con id {}", id);
        medicoRepository.deleteById(id);
    }

    public Medico actualizarMedico(Long id, @Valid Medico actualizado) {
        log.info("Actualizando médico con id {}", id);

        return medicoRepository.findById(id).map(medico -> {
            validarDatosUnicosEnActualizacion(id, actualizado);
            medico.setNombre(actualizado.getNombre());
            medico.setApellido(actualizado.getApellido());
            medico.setRut(actualizado.getRut());
            medico.setEspecialidad(actualizado.getEspecialidad());
            medico.setEmail(actualizado.getEmail());
            return medicoRepository.save(medico);
        }).orElseThrow(() -> {
            log.warn("Se intentó actualizar un médico inexistente con id {}", id);
            return new ResourceNotFoundException("Médico no encontrado con id: " + id);
        });
    }

    private void validarDatosUnicos(Medico medico) {
        if (medicoRepository.existsByRut(medico.getRut())) {
            throw new ReglaNegocioException("Ya existe un médico registrado con el RUT ingresado");
        }

        if (medicoRepository.existsByEmail(medico.getEmail())) {
            throw new ReglaNegocioException("Ya existe un médico registrado con el correo ingresado");
        }
    }

    private void validarDatosUnicosEnActualizacion(Long id, Medico actualizado) {
        if (medicoRepository.existsByRutAndIdNot(actualizado.getRut(), id)) {
            throw new ReglaNegocioException("El RUT ingresado pertenece a otro médico");
        }

        if (medicoRepository.existsByEmailAndIdNot(actualizado.getEmail(), id)) {
            throw new ReglaNegocioException("El correo ingresado pertenece a otro médico");
        }
    }
}
