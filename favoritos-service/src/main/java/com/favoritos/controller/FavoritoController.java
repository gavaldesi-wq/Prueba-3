package com.favoritos.controller;
 
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.favoritos.dto.FavoritoDTO;
import com.favoritos.service.FavoritoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
 
@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor

public class FavoritoController {
     private final FavoritoService favoritoService;
 
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(favoritoService.getAll());
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(favoritoService.getById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
 
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> getByUsuario(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(favoritoService.getByUsuario(usuarioId));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
 
    @PostMapping
    public ResponseEntity<?> save(
            @Valid @RequestBody FavoritoDTO dto,
            BindingResult bindingResult) {
 
        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(e ->
                    errores.put(e.getField(), e.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }
 
        try {
            return ResponseEntity.ok(favoritoService.save(dto));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
 
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody FavoritoDTO dto,
            BindingResult bindingResult) {
 
        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(e ->
                    errores.put(e.getField(), e.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }
 
        try {
            return ResponseEntity.ok(favoritoService.update(id, dto));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @RequestParam String correo,
            @RequestParam String password) {
        try {
            favoritoService.delete(id, correo, password);
            return ResponseEntity.ok(Map.of("mensaje", "Favorito eliminado correctamente"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
