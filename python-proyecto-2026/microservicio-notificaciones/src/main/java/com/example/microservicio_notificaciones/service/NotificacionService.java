package com.example.microservicio_notificaciones.service;

import com.example.microservicio_notificaciones.model.Notificacion;
import com.example.microservicio_notificaciones.repository.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);
    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public List<Notificacion> listarNotificaciones() {
        log.info("Listando registros de notificaciones");
        return notificacionRepository.findAll();
    }

    public Optional<Notificacion> obtenerNotificacionPorId(Long id) {
        log.info("Buscando notificacion con id {}", id);
        return notificacionRepository.findById(id);
    }

    public Notificacion guardarNotificacion(Notificacion notificacion) {
        log.info("Guardando notificacion");
        return notificacionRepository.save(notificacion);
    }

    public Optional<Notificacion> actualizarNotificacion(Long id, Notificacion actualizado) {
        log.info("Actualizando notificacion con id {}", id);
        return notificacionRepository.findById(id)
                .map(existente -> {
            existente.setUsuarioId(actualizado.getUsuarioId());
            existente.setMensaje(actualizado.getMensaje());
            existente.setCanal(actualizado.getCanal());
            existente.setEstado(actualizado.getEstado());
            existente.setFechaEnvio(actualizado.getFechaEnvio());
                    return notificacionRepository.save(existente);
                });
    }

    public boolean eliminarNotificacion(Long id) {
        if (!notificacionRepository.existsById(id)) {
            log.warn("No se encontró notificacion con id {} para eliminar", id);
            return false;
        }
        log.info("Eliminando notificacion con id {}", id);
        notificacionRepository.deleteById(id);
        return true;
    }
}
