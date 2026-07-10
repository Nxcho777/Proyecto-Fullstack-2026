package com.example.microservicio_recetas.service;

import com.example.microservicio_recetas.model.Receta;
import com.example.microservicio_recetas.repository.RecetaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecetaService {

    private static final Logger log = LoggerFactory.getLogger(RecetaService.class);
    private final RecetaRepository recetaRepository;

    public RecetaService(RecetaRepository recetaRepository) {
        this.recetaRepository = recetaRepository;
    }

    public List<Receta> listarRecetas() {
        log.info("Listando registros de recetas");
        return recetaRepository.findAll();
    }

    public Optional<Receta> obtenerRecetaPorId(Long id) {
        log.info("Buscando receta con id {}", id);
        return recetaRepository.findById(id);
    }

    public Receta guardarReceta(Receta receta) {
        log.info("Guardando receta");
        return recetaRepository.save(receta);
    }

    public Optional<Receta> actualizarReceta(Long id, Receta actualizado) {
        log.info("Actualizando receta con id {}", id);
        return recetaRepository.findById(id)
                .map(existente -> {
            existente.setPacienteId(actualizado.getPacienteId());
            existente.setMedicoId(actualizado.getMedicoId());
            existente.setMedicamento(actualizado.getMedicamento());
            existente.setDosis(actualizado.getDosis());
            existente.setIndicaciones(actualizado.getIndicaciones());
            existente.setFechaEmision(actualizado.getFechaEmision());
                    return recetaRepository.save(existente);
                });
    }

    public boolean eliminarReceta(Long id) {
        if (!recetaRepository.existsById(id)) {
            log.warn("No se encontró receta con id {} para eliminar", id);
            return false;
        }
        log.info("Eliminando receta con id {}", id);
        recetaRepository.deleteById(id);
        return true;
    }
}
