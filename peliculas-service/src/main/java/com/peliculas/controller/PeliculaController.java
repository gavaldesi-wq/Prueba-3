package com.peliculas.controller;

import com.peliculas.dto.GeneroDTO;
import com.peliculas.dto.PeliculaDTO;
import com.peliculas.service.GeneroService;
import com.peliculas.service.PeliculaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/peliculas")
@RequiredArgsConstructor
public class PeliculaController {

    private final PeliculaService peliculaService;
    private final GeneroService generoService;
    private static final Logger logger = LoggerFactory.getLogger(PeliculaController.class);

    // PELICULAS
    @GetMapping
    public ResponseEntity<?> getAllPeliculas() {
        logger.info("GET /api/peliculas");
        try {
            List<PeliculaDTO> peliculas = peliculaService.getAll();
            logger.debug("Cantidad de películas obtenidas: {}", peliculas.size());
            return ResponseEntity.ok(peliculas);
        } catch (RuntimeException ex) {
            logger.warn("Error obteniendo películas - {}", ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPeliculaById(@PathVariable Long id) {
        logger.info("GET /api/peliculas/{}", id);
        try {
            return ResponseEntity.ok(peliculaService.getById(id));
        } catch (RuntimeException ex) {
            logger.warn("Error buscando película id={} - {}", id, ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/genero/{generoId}")
    public ResponseEntity<?> getPeliculasByGenero(@PathVariable Long generoId) {
        logger.info("GET /api/peliculas/genero/{}", generoId);
        try {
            return ResponseEntity.ok(peliculaService.getByGenero(generoId));
        } catch (RuntimeException ex) {
            logger.warn("Error buscando películas por género={} - {}", generoId, ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/clasificacion/{clasificacion}")
    public ResponseEntity<?> getPeliculasByClasificacion(@PathVariable String clasificacion) {
        logger.info("GET /api/peliculas/clasificacion/{}", clasificacion);
        try {
            return ResponseEntity.ok(peliculaService.getByClasificacion(clasificacion));
        } catch (RuntimeException ex) {
            logger.warn("Error buscando películas por clasificación={} - {}", clasificacion, ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> savePelicula(@Valid @RequestBody PeliculaDTO dto, BindingResult bindingResult) {
        logger.info("POST /api/peliculas - titulo={}", dto.getTitulo());
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            logger.warn("Errores de validación: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            PeliculaDTO guardada = peliculaService.save(dto);
            logger.info("Película creada exitosamente id={}", guardada.getId());
            return ResponseEntity.ok(guardada);
        } catch (RuntimeException ex) {
            logger.warn("Error creando película titulo={} - {}", dto.getTitulo(), ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePelicula(@PathVariable Long id, @Valid @RequestBody PeliculaDTO dto, BindingResult bindingResult) {
        logger.info("PUT /api/peliculas/{} - titulo={}", id, dto.getTitulo());
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            logger.warn("Errores de validación: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            PeliculaDTO actualizado = peliculaService.update(id, dto);
            logger.info("Película actualizada exitosamente id={}", id);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException ex) {
            logger.warn("Error actualizando película id={} - {}", id, ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePelicula(@PathVariable Long id) {
        logger.info("DELETE /api/peliculas/{}", id);
        try {
            peliculaService.delete(id);
            logger.info("Película eliminada exitosamente id={}", id);
            return ResponseEntity.ok(Map.of("mensaje", "Película eliminada correctamente"));
        } catch (RuntimeException ex) {
            logger.warn("Error eliminando película id={} - {}", id, ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // GENEROS
    @GetMapping("/generos")
    public ResponseEntity<?> getAllGeneros() {
        logger.info("GET /api/peliculas/generos");
        try {
            List<GeneroDTO> generos = generoService.getAll();
            logger.debug("Cantidad de géneros obtenidos: {}", generos.size());
            return ResponseEntity.ok(generos);
        } catch (RuntimeException ex) {
            logger.warn("Error obteniendo géneros - {}", ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/generos/{id}")
    public ResponseEntity<?> getGeneroById(@PathVariable Long id) {
        logger.info("GET /api/peliculas/generos/{}", id);
        try {
            return ResponseEntity.ok(generoService.getById(id));
        } catch (RuntimeException ex) {
            logger.warn("Error buscando género id={} - {}", id, ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/generos")
    public ResponseEntity<?> saveGenero(@Valid @RequestBody GeneroDTO dto, BindingResult bindingResult) {
        logger.info("POST /api/peliculas/generos - nombre={}", dto.getNombre());
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            logger.warn("Errores de validación: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            GeneroDTO guardado = generoService.save(dto);
            logger.info("Género creado exitosamente id={}", guardado.getId());
            return ResponseEntity.ok(guardado);
        } catch (RuntimeException ex) {
            logger.warn("Error creando género nombre={} - {}", dto.getNombre(), ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/generos/{id}")
    public ResponseEntity<?> updateGenero(@PathVariable Long id, @Valid @RequestBody GeneroDTO dto, BindingResult bindingResult) {
        logger.info("PUT /api/peliculas/generos/{} - nombre={}", id, dto.getNombre());
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            logger.warn("Errores de validación: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            GeneroDTO actualizado = generoService.update(id, dto);
            logger.info("Género actualizado exitosamente id={}", id);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException ex) {
            logger.warn("Error actualizando género id={} - {}", id, ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/generos/{id}")
    public ResponseEntity<?> deleteGenero(@PathVariable Long id) {
        logger.info("DELETE /api/peliculas/generos/{}", id);
        try {
            generoService.delete(id);
            logger.info("Género eliminado exitosamente id={}", id);
            return ResponseEntity.ok(Map.of("mensaje", "Género eliminado correctamente"));
        } catch (RuntimeException ex) {
            logger.warn("Error eliminando género id={} - {}", id, ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}