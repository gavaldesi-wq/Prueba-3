package com.cinefunciones_service.controller;

import com.cinefunciones_service.dto.FuncionDTO;
import com.cinefunciones_service.service.FuncionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/funciones")
@RequiredArgsConstructor
public class FuncionController {

    private final FuncionService funcionService;
    private static final Logger logger = LoggerFactory.getLogger(FuncionController.class);

    /* Para mostrar todas las funciones */
    @GetMapping
    public ResponseEntity<?> getAll() {
        logger.info("GET /api/funciones");
        ResponseEntity<?> response = ResponseEntity.ok(funcionService.getAll());
        logger.debug("Funciónes obtenidas");
        return response;
    }

    /* Para buscar función por id */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        logger.info("GET /api/funciones/{}", id);
        return ResponseEntity.ok(funcionService.getById(id));
    }

    /* Para buscar funciones por película */
    @GetMapping("/pelicula/{peliculaId}")
    public ResponseEntity<?> getByPelicula(@PathVariable Long peliculaId) {
        logger.info("GET /api/funciones/pelicula/{}", peliculaId);
        return ResponseEntity.ok(funcionService.getByPelicula(peliculaId));
    }

    /* Para buscar funciones por sala */
    @GetMapping("/sala/{salaId}")
    public ResponseEntity<?> getBySala(@PathVariable Long salaId) {
        logger.info("GET /api/funciones/sala/{}", salaId);
        return ResponseEntity.ok(funcionService.getBySala(salaId));
    }

    /* Para buscar funciones por fecha */
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<?> getByFecha(@PathVariable String fecha) {
        logger.info("GET /api/funciones/fecha/{}", fecha);
        return ResponseEntity.ok(funcionService.getByFecha(fecha));
    }

    /* Para buscar funciones por estado */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> getByEstado(@PathVariable String estado) {
        logger.info("GET /api/funciones/estado/{}", estado);
        return ResponseEntity.ok(funcionService.getByEstado(estado));
    }

    /* Para mostrar solo funciones disponibles */
    @GetMapping("/disponibles")
    public ResponseEntity<?> getDisponibles() {
        logger.info("GET /api/funciones/disponibles");
        return ResponseEntity.ok(funcionService.getDisponibles());
    }

    /* Para buscar funciones por formato */
    @GetMapping("/formato/{formato}")
    public ResponseEntity<?> getByFormato(@PathVariable String formato) {
        logger.info("GET /api/funciones/formato/{}", formato);
        return ResponseEntity.ok(funcionService.getByFormato(formato));
    }

    /* Para buscar funciones por idioma */
    @GetMapping("/idioma/{idioma}")
    public ResponseEntity<?> getByIdioma(@PathVariable String idioma) {
        logger.info("GET /api/funciones/idioma/{}", idioma);
        return ResponseEntity.ok(funcionService.getByIdioma(idioma));
    }

    /* Para guardar una función */
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody FuncionDTO dto, BindingResult bindingResult) {
        logger.info("POST /api/funciones - peliculaId={} salaId={}", dto.getPeliculaId(), dto.getSalaId());
        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> 
                errores.put(error.getField(), error.getDefaultMessage())
            );
            logger.warn("Errores de validación: {}", errores);
            return ResponseEntity.badRequest().body(errores);
        }

        FuncionDTO guardada = funcionService.save(dto);
        logger.info("Función creada exitosamente id={}", guardada.getId());
        return ResponseEntity.ok(guardada);
    }

    /* Para actualizar una función */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody FuncionDTO dto, BindingResult bindingResult) {
        logger.info("PUT /api/funciones/{} - peliculaId={} salaId={}", id, dto.getPeliculaId(), dto.getSalaId());
        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> 
                errores.put(error.getField(), error.getDefaultMessage())
            );
            logger.warn("Errores de validación: {}", errores);
            return ResponseEntity.badRequest().body(errores);
        }

        return ResponseEntity.ok(funcionService.update(id, dto));
    }

    /* Para eliminar una función */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        logger.info("DELETE /api/funciones/{}", id);
        funcionService.delete(id);
        logger.info("Función eliminada exitosamente id={}", id);
        return ResponseEntity.ok(Map.of("mensaje", "Función eliminada correctamente"));
    }
}