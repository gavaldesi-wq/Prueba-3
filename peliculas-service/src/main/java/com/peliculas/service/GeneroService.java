package com.peliculas.service;

import com.peliculas.dto.GeneroDTO;
import com.peliculas.model.Genero;
import com.peliculas.repository.GeneroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeneroService {

    private final GeneroRepository generoRepository;

    public List<GeneroDTO> getAll() {
        log.info("Obteniendo todos los géneros");
        return generoRepository.findAll()
            .stream()
            .map(GeneroDTO::fromModel)
            .collect(Collectors.toList());
    }

    public GeneroDTO getById(Long id) {
        log.info("Buscando género id={}", id);
        Genero g = generoRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Género no encontrado id={}", id);
                return new RuntimeException("Género no encontrado");
            });
        log.info("Género encontrado id={}", id);
        return GeneroDTO.fromModel(g);
    }

    public GeneroDTO save(GeneroDTO dto) {
        log.info("Creando género nombre={}", dto.getNombre());
        Genero g = dto.toModel();
        Genero guardado = generoRepository.save(g);
        log.info("Género creado exitosamente id={}", guardado.getId());
        return GeneroDTO.fromModel(guardado);
    }

    public GeneroDTO update(Long id, GeneroDTO dto) {
        log.info("Actualizando género id={}", id);
        Genero g = generoRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Género no encontrado id={}", id);
                return new RuntimeException("Género no encontrado");
            });
        g.setNombre(dto.getNombre());
        Genero actualizado = generoRepository.save(g);
        log.info("Género actualizado exitosamente id={}", id);
        return GeneroDTO.fromModel(actualizado);
    }

    public void delete(Long id) {
        log.info("Eliminando género id={}", id);
        Genero genero = generoRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Género no encontrado id={}", id);
                return new RuntimeException("Género no encontrado");
            });
        generoRepository.delete(genero);
        log.info("Género eliminado exitosamente id={}", id);
    }
}