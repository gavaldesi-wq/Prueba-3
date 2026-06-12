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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeliculaService {

    private final PeliculaRepository peliculaRepository;
    private final GeneroRepository generoRepository;

    public List<PeliculaDTO> getAll() {
        log.info("Obteniendo todas las películas");
        return peliculaRepository.findAll()
            .stream()
            .map(PeliculaDTO::fromModel)
            .collect(Collectors.toList());
    }

    public PeliculaDTO getById(Long id) {
        log.info("Buscando película id={}", id);
        Pelicula p = peliculaRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Película no encontrada id={}", id);
                return new RuntimeException("Película no encontrada");
            });
        log.info("Película encontrada id={}", id);
        return PeliculaDTO.fromModel(p);
    }

    public List<PeliculaDTO> getByGenero(Long generoId) {
        log.info("Obteniendo películas por género id={}", generoId);
        return peliculaRepository.findByGeneroId(generoId)
            .stream()
            .map(PeliculaDTO::fromModel)
            .collect(Collectors.toList());
    }

    public List<PeliculaDTO> getByClasificacion(String clasificacion) {
        log.info("Obteniendo películas por clasificación={}", clasificacion);
        return peliculaRepository.findByClasificacion(clasificacion)
            .stream()
            .map(PeliculaDTO::fromModel)
            .collect(Collectors.toList());
    }

    public PeliculaDTO save(PeliculaDTO dto) {
        log.info("Creando película titulo={}", dto.getTitulo());
        Pelicula p = dto.toModel();
        if (dto.getGeneroId() != null) {
            Genero g = generoRepository.findById(dto.getGeneroId())
                .orElseThrow(() -> {
                    log.warn("Género no encontrado id={}", dto.getGeneroId());
                    return new RuntimeException("Género no encontrado");
                });
            p.setGenero(g);
        }
        Pelicula guardada = peliculaRepository.save(p);
        log.info("Película creada exitosamente id={}", guardada.getId());
        return PeliculaDTO.fromModel(guardada);
    }  

    public PeliculaDTO update(Long id, PeliculaDTO dto) {
        log.info("Actualizando película id={}", id);
        Pelicula p = peliculaRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Película no encontrada id={}", id);
                return new RuntimeException("Película no encontrada");
            });
        p.setTitulo(dto.getTitulo());
        p.setClasificacion(dto.getClasificacion());
        p.setDuracion(dto.getDuracion());
        p.setFechaEstreno(dto.getFechaEstreno());
        if (dto.getGeneroId() != null) {
            Genero g = generoRepository.findById(dto.getGeneroId())
                .orElseThrow(() -> {
                    log.warn("Género no encontrado id={}", dto.getGeneroId());
                    return new RuntimeException("Género no encontrado");
                });
            p.setGenero(g);
        }
        Pelicula actualizada = peliculaRepository.save(p);
        log.info("Película actualizada exitosamente id={}", id);
        return PeliculaDTO.fromModel(actualizada);
    }

    public void delete(Long id) {
        log.info("Eliminando película id={}", id);
        Pelicula pelicula = peliculaRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Película no encontrada id={}", id);
                return new RuntimeException("Película no encontrada");
            });
        peliculaRepository.delete(pelicula);
        log.info("Película eliminada exitosamente id={}", id);
    }
}