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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservasController {

    private final ReservasService reservasService;

     @GetMapping
    public ResponseEntity<?> getAll() {
        log.info("GET /api/reservas");
        return ResponseEntity.ok(reservasService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        log.info("GET /api/reservas/{}", id);
        return ResponseEntity.ok(reservasService.getById(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> getByUsuarioId(@PathVariable Long usuarioId) {
        log.info("GET /api/reservas/usuario/{}", usuarioId);
        return ResponseEntity.ok(reservasService.getByUsuarioId(usuarioId));
    }

    @PostMapping("/reservar")
    public ResponseEntity<?> comprar(
            @Valid @RequestBody CrearReservaRequestDTO request,
            BindingResult bindingResult) {

        log.info("POST /api/reservas/comprar - correo={} funcionId={}",
                request.getCorreo(),
                request.getFuncionId());

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());

            log.warn("Errores de validación al comprar reserva - {}", errors);

            return ResponseEntity.badRequest().body(errors);
        }

        ReservasDTO reserva = reservasService.crearReserva(request);
        log.info("Reserva creada exitosamente id={}", reserva.getId());
        return ResponseEntity.ok(reserva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.info("DELETE /api/reservas/{}", id);
        reservasService.delete(id);
        log.info("Reserva eliminada exitosamente id={}", id);
        return ResponseEntity.ok(Map.of("mensaje", "Reserva eliminada"));
    }
}
