package com.example.microservicio_historial_clinico.service;

import com.example.microservicio_historial_clinico.model.HistorialClinico;
import com.example.microservicio_historial_clinico.repository.HistorialClinicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistorialClinicoService {

    private static final Logger log = LoggerFactory.getLogger(HistorialClinicoService.class);
    private final HistorialClinicoRepository historialClinicoRepository;

    public HistorialClinicoService(HistorialClinicoRepository historialClinicoRepository) {
        this.historialClinicoRepository = historialClinicoRepository;
    }

    public List<HistorialClinico> listarHistorialesClinicos() {
        log.info("Listando registros de historiales-clinicos");
        return historialClinicoRepository.findAll();
    }

    public Optional<HistorialClinico> obtenerHistorialClinicoPorId(Long id) {
        log.info("Buscando historialClinico con id {}", id);
        return historialClinicoRepository.findById(id);
    }

    public HistorialClinico guardarHistorialClinico(HistorialClinico historialClinico) {
        log.info("Guardando historialClinico");
        return historialClinicoRepository.save(historialClinico);
    }

    public Optional<HistorialClinico> actualizarHistorialClinico(Long id, HistorialClinico actualizado) {
        log.info("Actualizando historialClinico con id {}", id);
        return historialClinicoRepository.findById(id)
                .map(existente -> {
            existente.setPacienteId(actualizado.getPacienteId());
            existente.setFechaRegistro(actualizado.getFechaRegistro());
            existente.setAntecedentes(actualizado.getAntecedentes());
            existente.setObservaciones(actualizado.getObservaciones());
            existente.setAlergias(actualizado.getAlergias());
                    return historialClinicoRepository.save(existente);
                });
    }

    public boolean eliminarHistorialClinico(Long id) {
        if (!historialClinicoRepository.existsById(id)) {
            log.warn("No se encontró historialClinico con id {} para eliminar", id);
            return false;
        }
        log.info("Eliminando historialClinico con id {}", id);
        historialClinicoRepository.deleteById(id);
        return true;
    }
}
