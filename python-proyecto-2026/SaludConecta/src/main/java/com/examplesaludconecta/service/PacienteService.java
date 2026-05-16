package com.examplesaludconecta.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.examplesaludconecta.model.Paciente;
import com.examplesaludconecta.repository.PacienteRepository;

@Service
@SuppressWarnings("null") 
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    public List<Paciente> obtenerTodosLosPacientes() {
        return pacienteRepository.findAll();
    }

    public Paciente guardarPaciente(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public Optional<Paciente> obtenerPorId(Long id) {
        return pacienteRepository.findById(id);
    }

    public void eliminarPaciente(Long id) {
        pacienteRepository.deleteById(id);
    }

    public Paciente actualizarPaciente(Long id, Paciente actualizado) {
        return pacienteRepository.findById(id).map(paciente -> {
            paciente.setNombre(actualizado.getNombre());
            paciente.setApellido(actualizado.getApellido());
            paciente.setRut(actualizado.getRut());
            return pacienteRepository.save(paciente);
        }).orElseThrow(() -> new RuntimeException("El id no pertecene a un Paciente: " + id));
    }
}