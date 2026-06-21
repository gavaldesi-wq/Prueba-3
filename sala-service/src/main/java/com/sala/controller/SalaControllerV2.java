package com.sala.controller;

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
import com.sala.assemblers.SalaModelAssembler;
import com.sala.dto.SalaDTO;
import com.sala.service.SalaService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("salas/v2")
public class SalaControllerV2 {
    private final SalaService salaService;
    private final SalaModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(SalaControllerV2.class);

    public SalaControllerV2(SalaService salaService, SalaModelAssembler assembler) {
        this.salaService = salaService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<SalaDTO>> listarSalas(HttpServletRequest request) {
        logger.info("V2 GET /salas - Listando salas");
        logger.info("X-Forwarded-Host: {}", request.getHeader("X-Forwarded-Host"));
        logger.info("X-Forwarded-Port: {}", request.getHeader("X-Forwarded-Port"));
        logger.info("X-Forwarded-Proto: {}", request.getHeader("X-Forwarded-Proto"));
        logger.info("Request URL (request.getRequestURL()): {}", request.getRequestURL());

        List<EntityModel<SalaDTO>> salas = salaService.getAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(salas, linkTo(methodOn(SalaControllerV2.class).listarSalas(null)).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<SalaDTO> obtenerSala(@PathVariable Long id) {
        logger.info("V2 GET /salas/{} - Obteniendo sala", id);
        SalaDTO sala = salaService.getById(id);
        return assembler.toModel(sala);
    }

    
}