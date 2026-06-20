package com.pagos.controller;

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
import com.pagos.assemblers.PagosModelAssembler;
import com.pagos.dto.PagosDTO;
import com.pagos.service.PagosService;

@RestController
@RequestMapping("pagos/v2")
public class PagosControllerV2 {
    private final PagosService pagosService;
    private final PagosModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(PagosControllerV2.class);

    public PagosControllerV2(PagosService pagosService, PagosModelAssembler assembler) {
        this.pagosService = pagosService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<PagosDTO>> listarPagos() {
        logger.info("V2 GET /pagos - Listando pagos");
        List<EntityModel<PagosDTO>> pagos = pagosService.getAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(pagos, linkTo(methodOn(PagosControllerV2.class).listarPagos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<PagosDTO> obtenerPago(@PathVariable Long id) {
        logger.info("V2 GET /pagos/{} - Obteniendo pago", id);
        PagosDTO pago = pagosService.getById(id);
        return assembler.toModel(pago);
    }

    @GetMapping("/reserva/{reservaId}")
    public CollectionModel<EntityModel<PagosDTO>> obtenerPagosPorReserva(@PathVariable Long reservaId) {
        logger.info("V2 GET /pagos/reserva/{} - Obteniendo pagos por reserva", reservaId);
        List<EntityModel<PagosDTO>> pagos = pagosService.getByReservaId(reservaId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(pagos,
                linkTo(methodOn(PagosControllerV2.class).obtenerPagosPorReserva(reservaId)).withSelfRel(),
                linkTo(methodOn(PagosControllerV2.class).listarPagos()).withRel("pagos"));
    }
}
