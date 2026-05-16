package com.examplesaludconecta.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.examplesaludconecta.model.Tratamiento;
import com.examplesaludconecta.repository.TratamientoRepository;

@Service
@SuppressWarnings("null") 
public class TratamientoService {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    public List<Tratamiento> obtenerTodosLosTratamientos() {
        return tratamientoRepository.findAll();
    }

    public Tratamiento guardarTratamiento(Tratamiento tratamiento) {
        return tratamientoRepository.save(tratamiento);
    }

    public Optional<Tratamiento> obtenerPorId(Long id) {
        return tratamientoRepository.findById(id);
    }

    public void eliminarTratamiento(Long id) {
        tratamientoRepository.deleteById(id);
    }

    public Tratamiento actualizarTratamiento(Long id, Tratamiento actualizado) {
        return tratamientoRepository.findById(id).map(tratamiento -> {
            tratamiento.setDiagnostico(actualizado.getDiagnostico());
            tratamiento.setMedicamento(actualizado.getMedicamento());
            tratamiento.setDosis(actualizado.getDosis());
            tratamiento.setDuracionDias(actualizado.getDuracionDias());
            return tratamientoRepository.save(tratamiento);
        }).orElseThrow(() -> new RuntimeException("El id no pertenece a un Tratamiento: " + id));
    }
}