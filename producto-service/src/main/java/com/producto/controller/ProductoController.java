package com.producto.controller;

import com.producto.DTO.ProductoDTO;
import com.producto.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Sistema para gestionar nuestros productos")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar productos", description = "Obtiene todos los productos registrados")
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(productoService.getAll());
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar producto por ID", description = "Obtiene un producto usando su ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productoService.getById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Crear producto", description = "Crea un nuevo producto")
    public ResponseEntity<?> save(@Valid @RequestBody ProductoDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();

            bindingResult.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );

            return ResponseEntity.badRequest().body(errores);
        }

        try {
            return ResponseEntity.ok(productoService.save(dto));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente usando su ID")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO dto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();

            bindingResult.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );

            return ResponseEntity.badRequest().body(errores);
        }

        try {
            return ResponseEntity.ok(productoService.update(id, dto));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto usando su ID")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            productoService.delete(id);
            return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado correctamente"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}

