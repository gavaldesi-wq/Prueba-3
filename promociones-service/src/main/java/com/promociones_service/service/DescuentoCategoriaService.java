package com.promociones_service.service;

import com.promociones_service.DTO.DescuentoCategoriaDTO;
import com.promociones_service.model.DescuentoCategoria;
import com.promociones_service.repository.DescuentoCategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import java.util.List;
import java.util.stream.Collectors;
 
@Service
@RequiredArgsConstructor
public class DescuentoCategoriaService {

    private final DescuentoCategoriaRepository descuentoCategoriaRepository;
 
    public List<DescuentoCategoriaDTO> getAll() {
        return descuentoCategoriaRepository.findAll()
                .stream()
                .map(DescuentoCategoriaDTO::fromModel)
                .collect(Collectors.toList());
    }
 
    public DescuentoCategoriaDTO getById(Long id) {
        DescuentoCategoria descuento = descuentoCategoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Descuento por categoría no encontrado"));
        return DescuentoCategoriaDTO.fromModel(descuento);
    }
 
    public DescuentoCategoriaDTO save(DescuentoCategoriaDTO dto) {
        String categoria = dto.getCategoria().toUpperCase();
 
        if (descuentoCategoriaRepository.existsByCategoria(categoria)) {
            throw new RuntimeException("Ya existe un descuento configurado para la categoría: " + categoria);
        }
 
        DescuentoCategoria descuento = dto.toModel();
        return DescuentoCategoriaDTO.fromModel(descuentoCategoriaRepository.save(descuento));
    }
 
    public DescuentoCategoriaDTO update(Long id, DescuentoCategoriaDTO dto) {
        DescuentoCategoria descuento = descuentoCategoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Descuento por categoría no encontrado"));
 
        String nuevaCategoria = dto.getCategoria().toUpperCase();
 
        // Si cambia la categoría, verificar que no exista ya otra con ese nombre
        if (!descuento.getCategoria().equals(nuevaCategoria)
                && descuentoCategoriaRepository.existsByCategoria(nuevaCategoria)) {
            throw new RuntimeException("Ya existe un descuento configurado para la categoría: " + nuevaCategoria);
        }
 
        descuento.setCategoria(nuevaCategoria);
        descuento.setMontoDescuento(dto.getMontoDescuento());
 
        return DescuentoCategoriaDTO.fromModel(descuentoCategoriaRepository.save(descuento));
    }
 
    public void delete(Long id) {
        DescuentoCategoria descuento = descuentoCategoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Descuento por categoría no encontrado"));
        descuentoCategoriaRepository.delete(descuento);
    }
}
