package com.examplesaludconecta.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.examplesaludconecta.model.Tratamiento;
import com.examplesaludconecta.service.TratamientoService;

@RestController
@RequestMapping("/api/tratamientos")
public class TratamientoController {

    @Autowired
    private TratamientoService tratamientoService;

    @GetMapping
    public List<Tratamiento> listarTratamientos() {
        return tratamientoService.obtenerTodosLosTratamientos();
    }

    @GetMapping("/{id}")
    public Optional<Tratamiento> buscarPorId(@PathVariable Long id) {
        return tratamientoService.obtenerPorId(id);
    }

    @PostMapping
    public Tratamiento guardarTratamiento(@RequestBody Tratamiento tratamiento) {
        return tratamientoService.guardarTratamiento(tratamiento);
    }

    @PutMapping("/{id}")
    public Tratamiento actualizar(@PathVariable Long id, @RequestBody Tratamiento tratamiento) {
        return tratamientoService.actualizarTratamiento(id, tratamiento);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        tratamientoService.eliminarTratamiento(id);
    }
}