package com.example.microservicio_diagnosticos.service;

import com.example.microservicio_diagnosticos.model.Diagnostico;
import com.example.microservicio_diagnosticos.repository.DiagnosticoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiagnosticoService {

    private static final Logger log = LoggerFactory.getLogger(DiagnosticoService.class);
    private final DiagnosticoRepository diagnosticoRepository;

    public DiagnosticoService(DiagnosticoRepository diagnosticoRepository) {
        this.diagnosticoRepository = diagnosticoRepository;
    }

    public List<Diagnostico> listarDiagnosticos() {
        log.info("Listando registros de diagnosticos");
        return diagnosticoRepository.findAll();
    }

    public Optional<Diagnostico> obtenerDiagnosticoPorId(Long id) {
        log.info("Buscando diagnostico con id {}", id);
        return diagnosticoRepository.findById(id);
    }

    public Diagnostico guardarDiagnostico(Diagnostico diagnostico) {
        log.info("Guardando diagnostico");
        return diagnosticoRepository.save(diagnostico);
    }

    public Optional<Diagnostico> actualizarDiagnostico(Long id, Diagnostico actualizado) {
        log.info("Actualizando diagnostico con id {}", id);
        return diagnosticoRepository.findById(id)
                .map(existente -> {
            existente.setPacienteId(actualizado.getPacienteId());
            existente.setMedicoId(actualizado.getMedicoId());
            existente.setDescripcion(actualizado.getDescripcion());
            existente.setGravedad(actualizado.getGravedad());
            existente.setFecha(actualizado.getFecha());
                    return diagnosticoRepository.save(existente);
                });
    }

    public boolean eliminarDiagnostico(Long id) {
        if (!diagnosticoRepository.existsById(id)) {
            log.warn("No se encontró diagnostico con id {} para eliminar", id);
            return false;
        }
        log.info("Eliminando diagnostico con id {}", id);
        diagnosticoRepository.deleteById(id);
        return true;
    }
}
