package com.sala.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> getAll(){
        logger.info("GET /api/salas");
        List<SalaDTO> salas = salaService.getAll();
        logger.debug("Cantidad de salas obtenidas: {}", salas.size());
        return ResponseEntity.ok(salas);
    }

    /*Para buscar sala por id */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        logger.info("GET /api/salas/{}", id);
        return ResponseEntity.ok(salaService.getById(id));
    }

    /*Para guardar una sala */
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody SalaDTO dto, BindingResult bindingResult) {
        logger.info("POST /api/salas - nombre={}", dto.getNombre());
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            logger.warn("Errores de validación: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }

        SalaDTO guardada = salaService.save(dto);
        logger.info("Sala creada exitosamente id={}", guardada.getId());
        return ResponseEntity.ok(guardada);
    }

    /*Para actualizar una sala */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody SalaDTO dto, BindingResult bindingResult) {
        logger.info("PUT /api/salas/{} - nombre={}", id, dto.getNombre());
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            logger.warn("Errores de validación: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }

        SalaDTO actualizado = salaService.update(id, dto);
        logger.info("Sala actualizada exitosamente id={}", id);
        return ResponseEntity.ok(actualizado);
    }

    /*Para eliminar una sala */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        logger.info("DELETE /api/salas/{}", id);
        salaService.delete(id);
        logger.info("Sala eliminada exitosamente id={}", id);
        return ResponseEntity.ok(Map.of("mensaje", "Sala eliminada correctamente"));
    }

}
