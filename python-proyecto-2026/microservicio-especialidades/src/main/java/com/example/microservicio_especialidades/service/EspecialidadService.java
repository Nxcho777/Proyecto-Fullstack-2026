package com.example.microservicio_especialidades.service;

import com.example.microservicio_especialidades.model.Especialidad;
import com.example.microservicio_especialidades.repository.EspecialidadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EspecialidadService {

    private static final Logger log = LoggerFactory.getLogger(EspecialidadService.class);
    private final EspecialidadRepository especialidadRepository;

    public EspecialidadService(EspecialidadRepository especialidadRepository) {
        this.especialidadRepository = especialidadRepository;
    }

    public List<Especialidad> listarEspecialidades() {
        log.info("Listando registros de especialidades");
        return especialidadRepository.findAll();
    }

    public Optional<Especialidad> obtenerEspecialidadPorId(Long id) {
        log.info("Buscando especialidad con id {}", id);
        return especialidadRepository.findById(id);
    }

    public Especialidad guardarEspecialidad(Especialidad especialidad) {
        log.info("Guardando especialidad");
        return especialidadRepository.save(especialidad);
    }

    public Optional<Especialidad> actualizarEspecialidad(Long id, Especialidad actualizado) {
        log.info("Actualizando especialidad con id {}", id);
        return especialidadRepository.findById(id)
                .map(existente -> {
            existente.setNombre(actualizado.getNombre());
            existente.setDescripcion(actualizado.getDescripcion());
            existente.setArea(actualizado.getArea());
            existente.setActiva(actualizado.getActiva());
                    return especialidadRepository.save(existente);
                });
    }

    public boolean eliminarEspecialidad(Long id) {
        if (!especialidadRepository.existsById(id)) {
            log.warn("No se encontró especialidad con id {} para eliminar", id);
            return false;
        }
        log.info("Eliminando especialidad con id {}", id);
        especialidadRepository.deleteById(id);
        return true;
    }
}
