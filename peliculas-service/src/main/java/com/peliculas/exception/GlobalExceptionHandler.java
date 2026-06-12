package com.peliculas.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("mensaje", "Error de validación");
        body.put("errores", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Error inesperado";
        HttpStatus status = determinarEstado(message);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("mensaje", message);
        return ResponseEntity.status(status).body(body);
    }

    private HttpStatus determinarEstado(String message) {
        String normalized = message.toLowerCase();
        if (normalized.contains("no encontrado") || normalized.contains("no existe") || normalized.contains("no se ha encontrado")) {
            return HttpStatus.NOT_FOUND;
        }
        if (normalized.contains("invalid") || normalized.contains("válid") || normalized.contains("valid") || normalized.contains("obligatorio") || normalized.contains("incorrect") || normalized.contains("debe ser")) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}