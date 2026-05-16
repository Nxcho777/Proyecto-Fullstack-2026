package com.examplesaludconecta.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.examplesaludconecta.model.Medico;
import com.examplesaludconecta.service.MedicoService;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @GetMapping
    public List<Medico> listarMedicos() {
        return medicoService.obtenerTodosLosMedicos();
    }

    @GetMapping("/{id}")
    public Optional<Medico> buscarPorId(@PathVariable Long id) {
        return medicoService.obtenerPorId(id);
    }

    @PostMapping
    public Medico guardarMedico(@RequestBody Medico medico) {
        return medicoService.guardarMedico(medico);
    }

    @PutMapping("/{id}")
    public Medico actualizar(@PathVariable Long id, @RequestBody Medico medico) {
        return medicoService.actualizarMedico(id, medico);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        medicoService.eliminarMedico(id);
    }
}