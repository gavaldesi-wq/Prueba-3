package com.producto.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.producto.DTO.ProductoDTO;
import com.producto.model.Producto;
import com.producto.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Devuelve todos los productos
    public List<ProductoDTO> getAll() {
        log.info("Obteniendo todos los productos");
        return productoRepository.findAll()
                .stream()
                .map(ProductoDTO::fromModel)
                .collect(Collectors.toList());
    }

      public ProductoDTO getByNombre(String nombre) {
            log.info("Buscando producto por nombre={}", nombre);
            return productoRepository.findByNombre(nombre)
            .stream()
            .findFirst()
            .map(ProductoDTO::fromModel)
            .orElseThrow(() -> {
                log.warn("Producto no encontrado por nombre={}", nombre);
                return new RuntimeException("Producto no encontrado");
            });
}

    // Busca un producto por su ID
    public ProductoDTO getById(Long id) {
        log.info("Buscando producto id={}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado id={}", id);
                    return new RuntimeException("Producto no encontrado");
                });

        log.info("Producto encontrado id={}", id);
        return ProductoDTO.fromModel(producto);
    }

    // Guarda un nuevo producto
    public ProductoDTO save(ProductoDTO dto) {
        log.info("Creando producto nombre={}", dto.getNombre());
        Producto producto = dto.toModel();
        Producto guardado = productoRepository.save(producto);
        log.info("Producto creado exitosamente id={}", guardado.getId());
        return ProductoDTO.fromModel(guardado);
    }

    // Actualiza un producto existente
    public ProductoDTO update(Long id, ProductoDTO dto) {
        log.info("Actualizando producto id={}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado id={}", id);
                    return new RuntimeException("Producto no encontrado");
                });

        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());

        Producto actualizado = productoRepository.save(producto);

        log.info("Producto actualizado exitosamente id={}", id);
        return ProductoDTO.fromModel(actualizado);
    }

    // Elimina un producto por ID
    public void delete(Long id) {
        log.info("Eliminando producto id={}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado id={}", id);
                    return new RuntimeException("Producto no encontrado");
                });

        productoRepository.delete(producto);
        log.info("Producto eliminado exitosamente id={}", id);
    }
    
}


