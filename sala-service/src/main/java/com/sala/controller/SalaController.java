package com.sala.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sala.dto.SalaDTO;
import com.sala.service.SalaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/salas")
@RequiredArgsConstructor
public class SalaController {

    private final SalaService salaService;
    private static final Logger logger = LoggerFactory.getLogger(SalaController.class);

    /*Para mostrar todas las salas */
    @GetMapping
    public List<SalaDTO> getAll(){
        logger.info("GET /api/salas");
        List<SalaDTO> salas = salaService.getAll();
        logger.debug("Cantidad de salas obtenidas: {}", salas.size());
        return salas;
    }

    /*Para buscar sala por id */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        logger.info("GET /api/salas/{}", id);
        try {
            return ResponseEntity.ok(salaService.getById(id));
        } catch (RuntimeException ex) {
            logger.warn("Error buscando sala id={} - {}", id, ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /*Para guardar una sala */
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody SalaDTO dto, BindingResult bindingResult) {
        logger.info("POST /api/salas - nombre={}", dto.getNombre());
        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> 
                errores.put(error.getField(), error.getDefaultMessage())
            );
            logger.warn("Errores de validación: {}", errores);
            return ResponseEntity.badRequest().body(errores);
        }

        try {
            SalaDTO guardada = salaService.save(dto);
            logger.info("Sala creada exitosamente id={}", guardada.getId());
            return ResponseEntity.ok(guardada);
        } catch (RuntimeException ex) {
            logger.warn("Error creando sala nombre={} - {}", dto.getNombre(), ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /*Para actualizar una sala */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody SalaDTO dto, BindingResult bindingResult) {
        logger.info("PUT /api/salas/{} - nombre={}", id, dto.getNombre());
        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> 
                errores.put(error.getField(), error.getDefaultMessage())
            );
            logger.warn("Errores de validación: {}", errores);
            return ResponseEntity.badRequest().body(errores);
        }

        try {
            SalaDTO actualizado = salaService.update(id, dto);
            logger.info("Sala actualizada exitosamente id={}", id);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException ex) {
            logger.warn("Error actualizando sala id={} - {}", id, ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /*Para eliminar una sala */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        logger.info("DELETE /api/salas/{}", id);
        try {
            salaService.delete(id);
            logger.info("Sala eliminada exitosamente id={}", id);
            return ResponseEntity.ok(Map.of("mensaje", "Sala eliminada correctamente"));
        } catch (RuntimeException ex) {
            logger.warn("Error eliminando sala id={} - {}", id, ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

}
