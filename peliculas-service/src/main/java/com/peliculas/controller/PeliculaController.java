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
        List<PeliculaDTO> peliculas = peliculaService.getAll();
        logger.debug("Cantidad de películas obtenidas: {}", peliculas.size());
        return ResponseEntity.ok(peliculas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPeliculaById(@PathVariable Long id) {
        logger.info("GET /api/peliculas/{}", id);
        return ResponseEntity.ok(peliculaService.getById(id));
    }

    @GetMapping("/genero/{generoId}")
    public ResponseEntity<?> getPeliculasByGenero(@PathVariable Long generoId) {
        logger.info("GET /api/peliculas/genero/{}", generoId);
        return ResponseEntity.ok(peliculaService.getByGenero(generoId));
    }

    @GetMapping("/clasificacion/{clasificacion}")
    public ResponseEntity<?> getPeliculasByClasificacion(@PathVariable String clasificacion) {
        logger.info("GET /api/peliculas/clasificacion/{}", clasificacion);
        return ResponseEntity.ok(peliculaService.getByClasificacion(clasificacion));
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
        PeliculaDTO guardada = peliculaService.save(dto);
        logger.info("Película creada exitosamente id={}", guardada.getId());
        return ResponseEntity.ok(guardada);
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
        PeliculaDTO actualizado = peliculaService.update(id, dto);
        logger.info("Película actualizada exitosamente id={}", id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePelicula(@PathVariable Long id) {
        logger.info("DELETE /api/peliculas/{}", id);
        peliculaService.delete(id);
        logger.info("Película eliminada exitosamente id={}", id);
        return ResponseEntity.ok(Map.of("mensaje", "Película eliminada correctamente"));
    }

    // GENEROS
    @GetMapping("/generos")
    public ResponseEntity<?> getAllGeneros() {
        logger.info("GET /api/peliculas/generos");
        List<GeneroDTO> generos = generoService.getAll();
        logger.debug("Cantidad de géneros obtenidos: {}", generos.size());
        return ResponseEntity.ok(generos);
    }

    @GetMapping("/generos/{id}")
    public ResponseEntity<?> getGeneroById(@PathVariable Long id) {
        logger.info("GET /api/peliculas/generos/{}", id);
        return ResponseEntity.ok(generoService.getById(id));
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
        GeneroDTO guardado = generoService.save(dto);
        logger.info("Género creado exitosamente id={}", guardado.getId());
        return ResponseEntity.ok(guardado);
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
        GeneroDTO actualizado = generoService.update(id, dto);
        logger.info("Género actualizado exitosamente id={}", id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/generos/{id}")
    public ResponseEntity<?> deleteGenero(@PathVariable Long id) {
        logger.info("DELETE /api/peliculas/generos/{}", id);
        generoService.delete(id);
        logger.info("Género eliminado exitosamente id={}", id);
        return ResponseEntity.ok(Map.of("mensaje", "Género eliminado correctamente"));
    }
}