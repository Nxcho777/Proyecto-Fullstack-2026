package com.example.microservicio_examenes.service;

import com.example.microservicio_examenes.model.Examen;
import com.example.microservicio_examenes.repository.ExamenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExamenService {

    private static final Logger log = LoggerFactory.getLogger(ExamenService.class);
    private final ExamenRepository examenRepository;

    public ExamenService(ExamenRepository examenRepository) {
        this.examenRepository = examenRepository;
    }

    public List<Examen> listarExamenes() {
        log.info("Listando registros de examenes");
        return examenRepository.findAll();
    }

    public Optional<Examen> obtenerExamenPorId(Long id) {
        log.info("Buscando examen con id {}", id);
        return examenRepository.findById(id);
    }

    public Examen guardarExamen(Examen examen) {
        log.info("Guardando examen");
        return examenRepository.save(examen);
    }

    public Optional<Examen> actualizarExamen(Long id, Examen actualizado) {
        log.info("Actualizando examen con id {}", id);
        return examenRepository.findById(id)
                .map(existente -> {
            existente.setPacienteId(actualizado.getPacienteId());
            existente.setTipoExamen(actualizado.getTipoExamen());
            existente.setResultado(actualizado.getResultado());
            existente.setEstado(actualizado.getEstado());
            existente.setFechaSolicitud(actualizado.getFechaSolicitud());
                    return examenRepository.save(existente);
                });
    }

    public boolean eliminarExamen(Long id) {
        if (!examenRepository.existsById(id)) {
            log.warn("No se encontró examen con id {} para eliminar", id);
            return false;
        }
        log.info("Eliminando examen con id {}", id);
        examenRepository.deleteById(id);
        return true;
    }
}
