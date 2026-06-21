package com.reservas.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.reservas.assemblers.ReservasModelAssembler;
import com.reservas.dto.ReservasDTO;
import com.reservas.service.ReservasService;

@RestController
@RequestMapping("reservas/v2")
public class ReservasControllerV2 {
    private final ReservasService reservasService;
    private final ReservasModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(ReservasControllerV2.class);

    public ReservasControllerV2(ReservasService reservasService, ReservasModelAssembler assembler) {
        this.reservasService = reservasService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<ReservasDTO>> listarReservas() {
        logger.info("V2 GET /reservas - Listando reservas");
        List<EntityModel<ReservasDTO>> reservas = reservasService.getAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(reservas, linkTo(methodOn(ReservasControllerV2.class).listarReservas()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<ReservasDTO> obtenerReserva(@PathVariable Long id) {
        logger.info("V2 GET /reservas/{} - Obteniendo reserva", id);
        ReservasDTO reserva = reservasService.getById(id);
        return assembler.toModel(reserva);
    }

    @GetMapping("/usuario/{usuarioId}")
    public CollectionModel<EntityModel<ReservasDTO>> obtenerReservasPorUsuario(@PathVariable Long usuarioId) {
        logger.info("V2 GET /reservas/usuario/{} - Obteniendo reservas por usuario", usuarioId);
        List<EntityModel<ReservasDTO>> reservas = reservasService.getByUsuarioId(usuarioId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(reservas, linkTo(methodOn(ReservasControllerV2.class).obtenerReservasPorUsuario(usuarioId)).withSelfRel());
    }
}
