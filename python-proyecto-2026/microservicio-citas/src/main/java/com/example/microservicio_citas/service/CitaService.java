package com.example.microservicio_citas.service;

import com.example.microservicio_citas.model.Cita;
import com.example.microservicio_citas.repository.CitaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitaService {

    private static final Logger log = LoggerFactory.getLogger(CitaService.class);
    private final CitaRepository citaRepository;

    public CitaService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    public List<Cita> listarCitas() {
        log.info("Listando registros de citas");
        return citaRepository.findAll();
    }

    public Optional<Cita> obtenerCitaPorId(Long id) {
        log.info("Buscando cita con id {}", id);
        return citaRepository.findById(id);
    }

    public Cita guardarCita(Cita cita) {
        log.info("Guardando cita");
        return citaRepository.save(cita);
    }

    public Optional<Cita> actualizarCita(Long id, Cita actualizado) {
        log.info("Actualizando cita con id {}", id);
        return citaRepository.findById(id)
                .map(existente -> {
            existente.setPacienteId(actualizado.getPacienteId());
            existente.setMedicoId(actualizado.getMedicoId());
            existente.setFecha(actualizado.getFecha());
            existente.setHora(actualizado.getHora());
            existente.setMotivo(actualizado.getMotivo());
            existente.setEstado(actualizado.getEstado());
                    return citaRepository.save(existente);
                });
    }

    public boolean eliminarCita(Long id) {
        if (!citaRepository.existsById(id)) {
            log.warn("No se encontró cita con id {} para eliminar", id);
            return false;
        }
        log.info("Eliminando cita con id {}", id);
        citaRepository.deleteById(id);
        return true;
    }
}
