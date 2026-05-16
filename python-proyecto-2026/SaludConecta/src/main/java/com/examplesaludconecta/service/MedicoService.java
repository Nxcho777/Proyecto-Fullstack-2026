package com.examplesaludconecta.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.examplesaludconecta.model.Medico;
import com.examplesaludconecta.repository.MedicoRepository;

@Service
@SuppressWarnings("null") 
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    public List<Medico> obtenerTodosLosMedicos() {
        return medicoRepository.findAll();
    }

    public Medico guardarMedico(Medico medico) {
        return medicoRepository.save(medico);
    }

    public Optional<Medico> obtenerPorId(Long id) {
        return medicoRepository.findById(id);
    }

    public void eliminarMedico(Long id) {
        medicoRepository.deleteById(id);
    }

    public Medico actualizarMedico(Long id, Medico actualizado) {
        return medicoRepository.findById(id).map(medico -> {
            medico.setNombre(actualizado.getNombre());
            medico.setApellido(actualizado.getApellido());
            medico.setRut(actualizado.getRut());
            medico.setEspecialidad(actualizado.getEspecialidad());
            return medicoRepository.save(medico);
        }).orElseThrow(() -> new RuntimeException("El id no pertenece a un Medico: " + id));
    }
}