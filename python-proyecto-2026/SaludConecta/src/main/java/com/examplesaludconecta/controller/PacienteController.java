package com.examplesaludconecta.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public List<Paciente> listarPacientes() {
        return pacienteService.obtenerTodosLosPacientes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> obtenerPacientePorId(@PathVariable Long id) {
        return pacienteService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

    @PostMapping
    public ResponseEntity<Paciente> crearPaciente(@Valid @RequestBody Paciente paciente) {
    Paciente nuevoPaciente = pacienteService.guardarPaciente(paciente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPaciente);
}

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizar(@PathVariable Long id, @Valid @RequestBody Paciente paciente) {
        Paciente actualizado = pacienteService.actualizarPaciente(id, paciente);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    pacienteService.eliminarPaciente(id);
        return ResponseEntity.noContent().build();
}
}