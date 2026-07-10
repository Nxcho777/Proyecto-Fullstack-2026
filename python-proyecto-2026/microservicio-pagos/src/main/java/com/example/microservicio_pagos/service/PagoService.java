package com.example.microservicio_pagos.service;

import com.example.microservicio_pagos.model.Pago;
import com.example.microservicio_pagos.repository.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);
    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public List<Pago> listarPagos() {
        log.info("Listando registros de pagos");
        return pagoRepository.findAll();
    }

    public Optional<Pago> obtenerPagoPorId(Long id) {
        log.info("Buscando pago con id {}", id);
        return pagoRepository.findById(id);
    }

    public Pago guardarPago(Pago pago) {
        log.info("Guardando pago");
        return pagoRepository.save(pago);
    }

    public Optional<Pago> actualizarPago(Long id, Pago actualizado) {
        log.info("Actualizando pago con id {}", id);
        return pagoRepository.findById(id)
                .map(existente -> {
            existente.setPacienteId(actualizado.getPacienteId());
            existente.setMonto(actualizado.getMonto());
            existente.setMetodoPago(actualizado.getMetodoPago());
            existente.setEstado(actualizado.getEstado());
            existente.setFechaPago(actualizado.getFechaPago());
                    return pagoRepository.save(existente);
                });
    }

    public boolean eliminarPago(Long id) {
        if (!pagoRepository.existsById(id)) {
            log.warn("No se encontró pago con id {} para eliminar", id);
            return false;
        }
        log.info("Eliminando pago con id {}", id);
        pagoRepository.deleteById(id);
        return true;
    }
}
