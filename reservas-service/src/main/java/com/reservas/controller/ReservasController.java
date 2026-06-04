package com.reservas.controller;

import com.reservas.dto.CrearReservaRequestDTO;
import com.reservas.dto.ReservasDTO;
import com.reservas.service.ReservasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservasController {

    private final ReservasService reservasService;

     @GetMapping
    public ResponseEntity<?> getAll() {
        log.info("GET /api/reservas");

        try {
            return ResponseEntity.ok(reservasService.getAll());
        } catch (RuntimeException ex) {
            log.warn("Error obteniendo reservas - {}", ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        log.info("GET /api/reservas/{}", id);

        try {
            return ResponseEntity.ok(reservasService.getById(id));
        } catch (RuntimeException ex) {
            log.warn("Error buscando reserva id={} - {}", id, ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> getByUsuarioId(@PathVariable Long usuarioId) {
        log.info("GET /api/reservas/usuario/{}", usuarioId);

        try {
            return ResponseEntity.ok(reservasService.getByUsuarioId(usuarioId));
        } catch (RuntimeException ex) {
            log.warn("Error buscando reservas del usuario id={} - {}", usuarioId, ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/reservar")
    public ResponseEntity<?> comprar(
            @Valid @RequestBody CrearReservaRequestDTO request,
            BindingResult bindingResult) {

        log.info("POST /api/reservas/comprar - correo={} funcionId={}",
                request.getCorreo(),
                request.getFuncionId());

        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();

            bindingResult.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );

            log.warn("Errores de validación al comprar reserva - {}", errores);

            return ResponseEntity.badRequest().body(errores);
        }

        try {

            ReservasDTO reserva = reservasService.crearReserva(request);

            log.info("Reserva creada exitosamente id={}",
                reserva.getId());

            return ResponseEntity.ok(reserva);

        } catch (RuntimeException ex) {
            log.warn("Error creando reserva para correo={} - {}",
                    request.getCorreo(),
                    ex.getMessage());

            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.info("DELETE /api/reservas/{}", id);

        try {
            reservasService.delete(id);

            log.info("Reserva eliminada exitosamente id={}", id);

            return ResponseEntity.ok(Map.of("mensaje", "Reserva eliminada"));
        } catch (RuntimeException ex) {
            log.warn("Error eliminando reserva id={} - {}", id, ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
