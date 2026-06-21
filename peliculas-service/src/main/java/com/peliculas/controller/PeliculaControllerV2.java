package com.peliculas.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.peliculas.assemblers.PeliculaModelAssembler;
import com.peliculas.dto.PeliculaDTO;
import com.peliculas.service.PeliculaService;

@RestController
@RequestMapping("peliculas/v2")
public class PeliculaControllerV2 {
    private final PeliculaService peliculaService;
    private final PeliculaModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(PeliculaControllerV2.class);

    public PeliculaControllerV2(PeliculaService peliculaService, PeliculaModelAssembler assembler) {
        this.peliculaService = peliculaService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<PeliculaDTO>> listarPeliculas() {
        logger.info("V2 GET /peliculas - Listando películas");
        List<EntityModel<PeliculaDTO>> peliculas = peliculaService.getAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(peliculas, linkTo(methodOn(PeliculaControllerV2.class).listarPeliculas()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<PeliculaDTO> obtenerPelicula(@PathVariable Long id) {
        logger.info("V2 GET /peliculas/{} - Obteniendo película", id);
        PeliculaDTO pelicula = peliculaService.getById(id);
        return assembler.toModel(pelicula);
    }

    @GetMapping("/genero/{generoId}")
    public CollectionModel<EntityModel<PeliculaDTO>> obtenerPeliculasPorGenero(@PathVariable Long generoId) {
        logger.info("V2 GET /peliculas/genero/{} - Obteniendo películas por género", generoId);
        List<EntityModel<PeliculaDTO>> peliculas = peliculaService.getByGenero(generoId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(peliculas, linkTo(methodOn(PeliculaControllerV2.class).obtenerPeliculasPorGenero(generoId)).withSelfRel());
    }

    @GetMapping("/clasificacion")
    public CollectionModel<EntityModel<PeliculaDTO>> obtenerPeliculasPorClasificacion(@RequestParam String clasificacion) {
        logger.info("V2 GET /peliculas/clasificacion - Obteniendo películas por clasificación={}", clasificacion);
        List<EntityModel<PeliculaDTO>> peliculas = peliculaService.getByClasificacion(clasificacion).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(peliculas, linkTo(methodOn(PeliculaControllerV2.class).obtenerPeliculasPorClasificacion(clasificacion)).withSelfRel());
    }
}
