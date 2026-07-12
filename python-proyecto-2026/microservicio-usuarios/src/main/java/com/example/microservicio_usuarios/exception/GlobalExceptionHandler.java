package com.example.microservicio_usuarios.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(ResourceNotFoundException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return crearRespuesta(HttpStatus.NOT_FOUND, "Elemento no encontrado", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarRestriccionDeBaseDeDatos(
            DataIntegrityViolationException ex
    ) {
        log.warn("La base de datos rechazó una operación por integridad de datos: {}", ex.getMessage());
        return crearRespuesta(
                HttpStatus.CONFLICT,
                "Conflicto de datos",
                "La operación infringe una restricción de integridad o contiene datos duplicados"
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        log.warn("Solicitud JSON inválida: {}", ex.getMessage());
        return crearRespuesta(
                HttpStatus.BAD_REQUEST,
                "Solicitud inválida",
                "El cuerpo JSON está incompleto o contiene un formato incorrecto"
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> manejarTipoDeParametroInvalido(
            MethodArgumentTypeMismatchException ex
    ) {
        return crearRespuesta(
                HttpStatus.BAD_REQUEST,
                "Parámetro inválido",
                "El parámetro " + ex.getName() + " no posee el tipo esperado"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(MethodArgumentNotValidException ex) {
        Map<String, Object> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> respuesta = respuestaBase(
                HttpStatus.BAD_REQUEST,
                "La validación de los datos ha resultado fallida"
        );
        respuesta.put("detalles", errores);

        return ResponseEntity.badRequest().body(respuesta);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacionDeParametros(ConstraintViolationException ex) {
        Map<String, Object> respuesta = respuestaBase(
                HttpStatus.BAD_REQUEST,
                "La validación de los parámetros ha resultado fallida"
        );
        respuesta.put("mensaje", ex.getMessage());
        return ResponseEntity.badRequest().body(respuesta);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> manejarReglaNegocio(ReglaNegocioException ex) {
        log.warn("Regla de negocio rechazada: {}", ex.getMessage());
        return crearRespuesta(HttpStatus.CONFLICT, "Regla de negocio no cumplida", ex.getMessage());
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> manejarCredencialesInvalidas(CredencialesInvalidasException ex) {
        log.warn("Intento de autenticación rechazado: {}", ex.getMessage());
        return crearRespuesta(HttpStatus.UNAUTHORIZED, "Credenciales inválidas", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErrorGeneral(Exception ex) {
        log.error("Error no controlado en microservicio-usuarios", ex);
        return crearRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                "Ha ocurrido un error inesperado al procesar la solicitud"
        );
    }

    private ResponseEntity<Map<String, Object>> crearRespuesta(
            HttpStatus estado,
            String error,
            String mensaje
    ) {
        Map<String, Object> respuesta = respuestaBase(estado, error);
        respuesta.put("mensaje", mensaje);
        return ResponseEntity.status(estado).body(respuesta);
    }

    private Map<String, Object> respuestaBase(HttpStatus estado, String error) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("estado", estado.value());
        respuesta.put("error", error);
        return respuesta;
    }
}
