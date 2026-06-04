package com.peliculas.service;

import com.peliculas.dto.GeneroDTO;
import com.peliculas.model.Genero;
import com.peliculas.repository.GeneroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeneroService {

    private final GeneroRepository generoRepository;

    public List<GeneroDTO> getAll() {
        return generoRepository.findAll()
            .stream()
            .map(GeneroDTO::fromModel)
            .collect(Collectors.toList());
    }

    public GeneroDTO getById(Long id) {
        Genero g = generoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Género no encontrado"));
        return GeneroDTO.fromModel(g);
    }

    public GeneroDTO save(GeneroDTO dto) {
        Genero g = dto.toModel();
        Genero guardado = generoRepository.save(g);
        return GeneroDTO.fromModel(guardado);
    }

    public GeneroDTO update(Long id, GeneroDTO dto) {
        Genero g = generoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Género no encontrado"));
        g.setNombre(dto.getNombre());
        Genero actualizado = generoRepository.save(g);
        return GeneroDTO.fromModel(actualizado);
    }

    public void delete(Long id) {
        generoRepository.deleteById(id);
    }
}