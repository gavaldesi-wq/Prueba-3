package com.producto.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.producto.DTO.ProductoDTO;
import com.producto.model.Producto;
import com.producto.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Devuelve todos los productos
    public List<ProductoDTO> getAll() {
        return productoRepository.findAll()
                .stream()
                .map(ProductoDTO::fromModel)
                .collect(Collectors.toList());
    }

    // Busca un producto por su ID
    public ProductoDTO getById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return ProductoDTO.fromModel(producto);
    }

    // Guarda un nuevo producto
    public ProductoDTO save(ProductoDTO dto) {
        Producto producto = dto.toModel();
        Producto guardado = productoRepository.save(producto);
        return ProductoDTO.fromModel(guardado);
    }

    // Actualiza un producto existente
    public ProductoDTO update(Long id, ProductoDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());

        Producto actualizado = productoRepository.save(producto);

        return ProductoDTO.fromModel(actualizado);
    }

    // Elimina un producto por ID
    public void delete(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        productoRepository.delete(producto);
    }
    
}


