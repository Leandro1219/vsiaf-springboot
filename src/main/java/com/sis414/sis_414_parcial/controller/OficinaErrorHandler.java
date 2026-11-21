package com.sis414.sis_414_parcial.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/** Respuestas legibles para los endpoints de OFICINA; no cambia los de RESPONSABLE. */
@RestControllerAdvice(assignableTypes = {OficinaController.class, OficinaPantallaController.class})
public class OficinaErrorHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> manejarRespuesta(ResponseStatusException error) {
        int codigo = error.getStatusCode().value();
        return ResponseEntity.status(error.getStatusCode()).body(Map.of(
                "status", codigo,
                "mensaje", error.getReason() == null ? "Solicitud no valida" : error.getReason()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarIntegridad(DataIntegrityViolationException error) {
        return ResponseEntity.status(409).body(Map.of(
                "status", 409,
                "mensaje", "La operacion contradice una restriccion de la base de datos"));
    }
}
