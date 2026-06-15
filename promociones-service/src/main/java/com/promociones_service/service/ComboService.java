package com.promociones_service.service;

import com.promociones_service.DTO.ComboDTO;
import com.promociones_service.DTO.*;
import com.promociones_service.model.Combo;
import com.promociones_service.repository.ComboRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComboService {

    private final ComboRepository comboRepository;
    private final RestTemplate restTemplate;

    public List<ComboDTO> getAll() {
        log.info("Obteniendo todos los combos");
        return comboRepository.findAll()
                .stream()
                .map(ComboDTO::fromModel)
                .collect(Collectors.toList());
    }

    public List<ComboDTO> getActivos() {
        log.info("Obteniendo combos activos");
        return comboRepository.findByActivoTrue()
                .stream()
                .map(ComboDTO::fromModel)
                .collect(Collectors.toList());
    }

    public ComboDTO getById(Long id) {
        log.info("Buscando combo id={}", id);
        Combo combo = comboRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Combo no encontrado id={}", id);
                    return new RuntimeException("Combo no encontrado");
                });
        return ComboDTO.fromModel(combo);
    }

    public ComboDTO getByNombre(String nombre) {
        log.info("Buscando combo nombre={}", nombre);
        Combo combo = comboRepository.findByNombreAndActivoTrue(nombre)
                .orElseThrow(() -> {
                    log.warn("Combo no encontrado nombre={}", nombre);
                    return new RuntimeException("El combo '" + nombre + "' no existe o no está activo");
                });
        return ComboDTO.fromModel(combo);
    }

    public ComboDTO save(ComboDTO dto) {
        log.info("Creando combo nombre={}", dto.getNombre());
        validarProductos(dto.getProductos());
        Combo combo = dto.toModel();
        Combo guardado = comboRepository.save(combo);
        log.info("Combo creado exitosamente id={}", guardado.getId());
        return ComboDTO.fromModel(guardado);
    }

    public ComboDTO update(Long id, ComboDTO dto) {
        log.info("Actualizando combo id={}", id);
        Combo combo = comboRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Combo no encontrado id={}", id);
                    return new RuntimeException("Combo no encontrado");
                });

        validarProductos(dto.getProductos());

        combo.setNombre(dto.getNombre());
        combo.setDescripcion(dto.getDescripcion());
        combo.setPrecioCombo(dto.getPrecioCombo());
        if (dto.getActivo() != null) combo.setActivo(dto.getActivo());

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            combo.setProductosJson(mapper.writeValueAsString(dto.getProductos()));
        } catch (Exception e) {
            log.error("Error procesando productos del combo id={}", id, e);
            throw new RuntimeException("Error procesando productos del combo");
        }

        Combo actualizado = comboRepository.save(combo);
        log.info("Combo actualizado exitosamente id={}", id);
        return ComboDTO.fromModel(actualizado);
    }

    public void delete(Long id) {
        log.info("Eliminando combo id={}", id);
        Combo combo = comboRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Combo no encontrado id={}", id);
                    return new RuntimeException("Combo no encontrado");
                });
        comboRepository.delete(combo);
        log.info("Combo eliminado exitosamente id={}", id);
    }

    private void validarProductos(List<ProductoComboDTO> productos) {
        for (ProductoComboDTO item : productos) {
            try {
                String url = "http://producto-service:8087/api/productos/nombre/" + item.getNombre();
                restTemplate.getForObject(url, Map.class);
                log.debug("Producto validado nombre={}", item.getNombre());
            } catch (RestClientException e) {
                log.warn("Producto no encontrado nombre={}", item.getNombre());
                throw new RuntimeException("El producto '" + item.getNombre() + "' no existe");
            }
        }
    }
}