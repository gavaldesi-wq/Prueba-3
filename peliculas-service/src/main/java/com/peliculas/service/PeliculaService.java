package com.peliculas.service;

import com.peliculas.dto.PeliculaDTO;
import com.peliculas.model.Genero;
import com.peliculas.model.Pelicula;
import com.peliculas.repository.GeneroRepository;
import com.peliculas.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PeliculaService {

    private final PeliculaRepository peliculaRepository;
    private final GeneroRepository generoRepository;

    public List<PeliculaDTO> getAll() {
        return peliculaRepository.findAll()
            .stream()
            .map(PeliculaDTO::fromModel)
            .collect(Collectors.toList());
    }

    public PeliculaDTO getById(Long id) {
        Pelicula p = peliculaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Película no encontrada"));
        return PeliculaDTO.fromModel(p);
    }

    public List<PeliculaDTO> getByGenero(Long generoId) {
        return peliculaRepository.findByGeneroId(generoId)
            .stream()
            .map(PeliculaDTO::fromModel)
            .collect(Collectors.toList());
    }

    public List<PeliculaDTO> getByClasificacion(String clasificacion) {
        return peliculaRepository.findByClasificacion(clasificacion)
            .stream()
            .map(PeliculaDTO::fromModel)
            .collect(Collectors.toList());
    }

    public PeliculaDTO save(PeliculaDTO dto) {
        Pelicula p = dto.toModel();
        if (dto.getGeneroId() != null) {
            Genero g = generoRepository.findById(dto.getGeneroId())
                .orElseThrow(() -> new RuntimeException("Género no encontrado"));
            p.setGenero(g);
        }
        Pelicula guardada = peliculaRepository.save(p);
        return PeliculaDTO.fromModel(guardada);
    }  

    public PeliculaDTO update(Long id, PeliculaDTO dto) {
        Pelicula p = peliculaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Película no encontrada"));
        p.setTitulo(dto.getTitulo());
        p.setClasificacion(dto.getClasificacion());
        p.setDuracion(dto.getDuracion());
        p.setFechaEstreno(dto.getFechaEstreno());
        if (dto.getGeneroId() != null) {
            Genero g = generoRepository.findById(dto.getGeneroId())
                .orElseThrow(() -> new RuntimeException("Género no encontrado"));
            p.setGenero(g);
        }
        Pelicula actualizada = peliculaRepository.save(p);
        return PeliculaDTO.fromModel(actualizada);
    }

    public void delete(Long id) {
        peliculaRepository.deleteById(id);
    }
}