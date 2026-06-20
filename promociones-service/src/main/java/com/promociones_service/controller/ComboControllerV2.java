package com.promociones_service.controller;

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
import com.promociones_service.assemblers.ComboModelAssembler;
import com.promociones_service.DTO.ComboDTO;
import com.promociones_service.service.ComboService;

@RestController
@RequestMapping("combos/v2")
public class ComboControllerV2 {
    private final ComboService comboService;
    private final ComboModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(ComboControllerV2.class);

    public ComboControllerV2(ComboService comboService, ComboModelAssembler assembler) {
        this.comboService = comboService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<ComboDTO>> listarCombos() {
        logger.info("V2 GET /combos - Listando combos");
        List<EntityModel<ComboDTO>> combos = comboService.getAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(combos, linkTo(methodOn(ComboControllerV2.class).listarCombos()).withSelfRel());
    }

    @GetMapping("/activos")
    public CollectionModel<EntityModel<ComboDTO>> listarCombosActivos() {
        logger.info("V2 GET /combos/activos - Listando combos activos");
        List<EntityModel<ComboDTO>> combos = comboService.getActivos().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(combos, 
                linkTo(methodOn(ComboControllerV2.class).listarCombosActivos()).withSelfRel(),
                linkTo(methodOn(ComboControllerV2.class).listarCombos()).withRel("combos"));
    }

    @GetMapping("/{id}")
    public EntityModel<ComboDTO> obtenerCombo(@PathVariable Long id) {
        logger.info("V2 GET /combos/{} - Obteniendo combo", id);
        ComboDTO combo = comboService.getById(id);
        return assembler.toModel(combo);
    }

    @GetMapping("/nombre/{nombre}")
    public EntityModel<ComboDTO> obtenerComboPorNombre(@PathVariable String nombre) {
        logger.info("V2 GET /combos/nombre/{} - Obteniendo combo por nombre", nombre);
        ComboDTO combo = comboService.getByNombre(nombre);
        return assembler.toModel(combo);
    }
}
