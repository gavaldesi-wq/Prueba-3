package com.promociones_service.controller;
 
import java.util.HashMap;
import java.util.Map;

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

import com.promociones_service.DTO.DescuentoCategoriaDTO;
import com.promociones_service.service.DescuentoCategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
 
@RestController
@RequestMapping("/api/descuentos-categoria")
@RequiredArgsConstructor
public class DescuentoCategoriaController {
    
    private final DescuentoCategoriaService descuentoCategoriaService;
 
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(descuentoCategoriaService.getAll());
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(descuentoCategoriaService.getById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
 
    @PostMapping
    public ResponseEntity<?> save(
            @Valid @RequestBody DescuentoCategoriaDTO dto,
            BindingResult bindingResult) {
 
        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(e ->
                    errores.put(e.getField(), e.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }
 
        try {
            return ResponseEntity.ok(descuentoCategoriaService.save(dto));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
 
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody DescuentoCategoriaDTO dto,
            BindingResult bindingResult) {
 
        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(e ->
                    errores.put(e.getField(), e.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }
 
        try {
            return ResponseEntity.ok(descuentoCategoriaService.update(id, dto));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            descuentoCategoriaService.delete(id);
            return ResponseEntity.ok(Map.of("mensaje", "Descuento eliminado correctamente"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
