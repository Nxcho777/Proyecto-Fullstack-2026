package com.examplesaludconecta.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.examplesaludconecta.model.Tratamiento;
import com.examplesaludconecta.service.TratamientoService;

import jakarta.validation.Valid;

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
    public ResponseEntity<Tratamiento> buscarPorId(@PathVariable Long id) {
        return tratamientoService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tratamiento> guardarTratamiento(
    @Valid @RequestBody Tratamiento tratamiento) {
    Tratamiento nuevo = tratamientoService.guardarTratamiento(tratamiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tratamiento> actualizar(
    @PathVariable Long id,
    @Valid @RequestBody Tratamiento tratamiento) {
    Tratamiento actualizado =
            tratamientoService.actualizarTratamiento(id, tratamiento);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    tratamientoService.eliminarTratamiento(id);
        return ResponseEntity.noContent().build();
    }
}