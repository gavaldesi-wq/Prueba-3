package com.favoritos.controller;

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
import com.favoritos.assemblers.FavoritoModelAssembler;
import com.favoritos.dto.FavoritoDTO;
import com.favoritos.service.FavoritoService;

@RestController
@RequestMapping("favoritos/v2")
public class FavoritoControllerV2 {
    private final FavoritoService favoritoService;
    private final FavoritoModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(FavoritoControllerV2.class);

    public FavoritoControllerV2(FavoritoService favoritoService, FavoritoModelAssembler assembler) {
        this.favoritoService = favoritoService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<FavoritoDTO>> listarFavoritos() {
        logger.info("V2 GET /favoritos - Listando favoritos");
        List<EntityModel<FavoritoDTO>> favoritos = favoritoService.getAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(favoritos, linkTo(methodOn(FavoritoControllerV2.class).listarFavoritos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<FavoritoDTO> obtenerFavorito(@PathVariable Long id) {
        logger.info("V2 GET /favoritos/{} - Obteniendo favorito", id);
        FavoritoDTO favorito = favoritoService.getById(id);
        return assembler.toModel(favorito);
    }

    @GetMapping("/usuario/{usuarioId}")
    public CollectionModel<EntityModel<FavoritoDTO>> obtenerFavoritosPorUsuario(@PathVariable Long usuarioId) {
        logger.info("V2 GET /favoritos/usuario/{} - Obteniendo favoritos por usuario", usuarioId);
        List<EntityModel<FavoritoDTO>> favoritos = favoritoService.getByUsuario(usuarioId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(favoritos,
                linkTo(methodOn(FavoritoControllerV2.class).obtenerFavoritosPorUsuario(usuarioId)).withSelfRel(),
                linkTo(methodOn(FavoritoControllerV2.class).listarFavoritos()).withRel("favoritos"));
    }
}
