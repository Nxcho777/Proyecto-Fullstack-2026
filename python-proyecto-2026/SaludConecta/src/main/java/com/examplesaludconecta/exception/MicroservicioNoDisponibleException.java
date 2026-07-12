package com.examplesaludconecta.exception;

public class MicroservicioNoDisponibleException extends RuntimeException {

    public MicroservicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
