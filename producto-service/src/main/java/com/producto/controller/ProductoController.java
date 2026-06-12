package com.producto.controller;

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

import com.producto.DTO.ProductoDTO;
import com.producto.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    @GetMapping
    public ResponseEntity<?> getAll() {
        logger.info("GET /api/productos");
        List<ProductoDTO> productos = productoService.getAll();
        logger.debug("Cantidad de productos obtenidos: {}", productos.size());
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<?> getByNombre(@PathVariable String nombre) {
        logger.info("GET /api/productos/nombre/{}", nombre);
        return ResponseEntity.ok(productoService.getByNombre(nombre));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        logger.info("GET /api/productos/{}", id);
        return ResponseEntity.ok(productoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody ProductoDTO dto, BindingResult bindingResult) {
        logger.info("POST /api/productos - nombre={}", dto.getNombre());
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            logger.warn("Errores de validación: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }
        ProductoDTO guardado = productoService.save(dto);
        logger.info("Producto creado exitosamente id={}", guardado.getId());
        return ResponseEntity.ok(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto, BindingResult bindingResult) {
        logger.info("PUT /api/productos/{} - nombre={}", id, dto.getNombre());
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            logger.warn("Errores de validación: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }
        ProductoDTO actualizado = productoService.update(id, dto);
        logger.info("Producto actualizado exitosamente id={}", id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        logger.info("DELETE /api/productos/{}", id);
        productoService.delete(id);
        logger.info("Producto eliminado exitosamente id={}", id);
        return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado correctamente"));
    }
}