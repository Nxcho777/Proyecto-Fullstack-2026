package com.examplesaludconecta.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examplesaludconecta.model.Paciente;
import com.examplesaludconecta.service.PacienteService;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    // Obtener todos: GET http://localhost:8080/api/pacientes
    @GetMapping
    public List<Paciente> listarPacientes() {
        return pacienteService.obtenerTodosLosPacientes();
    }

    // Buscar por ID: GET http://localhost:8080/api/pacientes/{id}
    @GetMapping("/{id}")
    public Optional<Paciente> buscarPorId(@PathVariable Long id) {
        return pacienteService.obtenerPorId(id);
    }

    // Guardar nuevo: POST http://localhost:8080/api/pacientes
    @PostMapping
    public Paciente guardarPaciente(@RequestBody Paciente paciente) {
        return pacienteService.guardarPaciente(paciente);
    }

    // Actualizar: PUT http://localhost:8080/api/pacientes/{id}
    @PutMapping("/{id}")
    public Paciente actualizar(@PathVariable Long id, @RequestBody Paciente paciente) {
        return pacienteService.actualizarPaciente(id, paciente);
    }

    // Eliminar: DELETE http://localhost:8080/api/pacientes/{id}
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        pacienteService.eliminarPaciente(id);
    }
}