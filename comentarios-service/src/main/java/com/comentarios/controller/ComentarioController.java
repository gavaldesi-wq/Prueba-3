package com.comentarios.controller;

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

import com.comentarios.dto.ComentarioDTO;
import com.comentarios.service.ComentarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
 
@RestController
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
public class ComentarioController {
   private final ComentarioService comentarioService;
 
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(comentarioService.getAll());
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(comentarioService.getById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
 
    @GetMapping("/pelicula/{peliculaId}")
    public ResponseEntity<?> getByPelicula(@PathVariable Long peliculaId) {
        try {
            return ResponseEntity.ok(comentarioService.getByPelicula(peliculaId));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
 
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> getByUsuario(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(comentarioService.getByUsuario(usuarioId));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
 
    @PostMapping
    public ResponseEntity<?> save(
            @Valid @RequestBody ComentarioDTO dto,
            BindingResult bindingResult) {
 
        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(e ->
                    errores.put(e.getField(), e.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }
 
        try {
            return ResponseEntity.ok(comentarioService.save(dto));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
 
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody ComentarioDTO dto,
            BindingResult bindingResult) {
 
        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(e ->
                    errores.put(e.getField(), e.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }
 
        try {
            return ResponseEntity.ok(comentarioService.update(id, dto));
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
            comentarioService.delete(id, correo, password);
            return ResponseEntity.ok(Map.of("mensaje", "Comentario eliminado correctamente"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
