package com.pagos.controller;

import com.pagos.dto.BoletaDTO;
import com.pagos.dto.CrearPagoRequestDTO;
import com.pagos.dto.PagosDTO;
import com.pagos.service.PagosService;
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
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagosController {

    private final PagosService pagosService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        log.info("GET /api/pagos");
        try {
            return ResponseEntity.ok(
                    pagosService.getAll()
            );
        } catch (RuntimeException ex) {
            log.warn("Error obteniendo pagos - {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        log.info("GET /api/pagos/{}", id);
        try {
            return ResponseEntity.ok(
                    pagosService.getById(id)
            );
        } catch (RuntimeException ex) {
            log.warn("Error buscando pago id={} - {}", id, ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/reserva/{reservaId}")
    public ResponseEntity<?> getByReservaId(@PathVariable Long reservaId) {
        log.info("GET /api/pagos/reserva/{}", reservaId);
        try {
            return ResponseEntity.ok(
                    pagosService.getByReservaId(reservaId)
            );
        } catch (RuntimeException ex) {
            log.warn("Error buscando pagos de reserva={} - {}",
                    reservaId,
                    ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/pagar")
    public ResponseEntity<?> pagarReserva(
            @Valid @RequestBody CrearPagoRequestDTO request,
            BindingResult bindingResult) {

        log.info("POST /api/pagos/pagar - reservaId={}",
                request.getReservaId());
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            log.warn("Errores de validación al crear pago - {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            BoletaDTO boleta = pagosService.pagarReserva(request);
             log.info("Pago creado exitosamente id={}", boleta.getPagoId());
        return ResponseEntity.ok(boleta);
        } catch (RuntimeException ex) {
            log.warn("Error procesando pago - {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody PagosDTO dto,
            BindingResult bindingResult) {
        log.info("PUT /api/pagos/{}", id);
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            return ResponseEntity.ok(
                    pagosService.update(id, dto)
            );

        } catch (RuntimeException ex) {

            log.warn("Error actualizando pago id={} - {}",
                    id,
                    ex.getMessage());

            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        log.info("DELETE /api/pagos/{}", id);
        try {
            pagosService.delete(id);
            log.info("Pago eliminado exitosamente id={}", id);
            return ResponseEntity.ok(
                    Map.of("mensaje", "Pago eliminado")
            );
        } catch (RuntimeException ex) {
            log.warn("Error eliminando pago id={} - {}",
                    id,
                    ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}