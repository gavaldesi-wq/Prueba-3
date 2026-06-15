package com.promociones_service.controller;

import com.promociones_service.DTO.*;
import com.promociones_service.service.ComboService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/combos")
@RequiredArgsConstructor
public class ComboController {

    private final ComboService comboService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        log.info("GET /api/combos");
        return ResponseEntity.ok(comboService.getAll());
    }

    @GetMapping("/activos")
    public ResponseEntity<?> getActivos() {
        log.info("GET /api/combos/activos");
        return ResponseEntity.ok(comboService.getActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        log.info("GET /api/combos/{}", id);
        return ResponseEntity.ok(comboService.getById(id));
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<?> getByNombre(@PathVariable String nombre) {
        log.info("GET /api/combos/nombre/{}", nombre);
        return ResponseEntity.ok(comboService.getByNombre(nombre));
    }

    @PostMapping
    public ResponseEntity<?> save(
            @Valid @RequestBody ComboDTO dto,
            BindingResult bindingResult) {
        log.info("POST /api/combos - nombre={}", dto.getNombre());
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            log.warn("Errores de validación al crear combo - {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }
        ComboDTO guardado = comboService.save(dto);
        log.info("Combo creado exitosamente id={}", guardado.getId());
        return ResponseEntity.ok(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody ComboDTO dto,
            BindingResult bindingResult) {
        log.info("PUT /api/combos/{}", id);
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok(comboService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.info("DELETE /api/combos/{}", id);
        comboService.delete(id);
        log.info("Combo eliminado exitosamente id={}", id);
        return ResponseEntity.ok(Map.of("mensaje", "Combo eliminado"));
    }
}