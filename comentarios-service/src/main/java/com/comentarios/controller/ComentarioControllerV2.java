package com.comentarios.controller;

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
import com.comentarios.assemblers.ComentarioModelAssembler;
import com.comentarios.dto.ComentarioDTO;
import com.comentarios.service.ComentarioService;

@RestController
@RequestMapping("comentarios/v2")
public class ComentarioControllerV2 {
    private final ComentarioService comentarioService;
    private final ComentarioModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(ComentarioControllerV2.class);

    public ComentarioControllerV2(ComentarioService comentarioService, ComentarioModelAssembler assembler) {
        this.comentarioService = comentarioService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<ComentarioDTO>> listarComentarios() {
        logger.info("V2 GET /comentarios - Listando comentarios");
        List<EntityModel<ComentarioDTO>> comentarios = comentarioService.getAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(comentarios, linkTo(methodOn(ComentarioControllerV2.class).listarComentarios()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<ComentarioDTO> obtenerComentario(@PathVariable Long id) {
        logger.info("V2 GET /comentarios/{} - Obteniendo comentario", id);
        ComentarioDTO comentario = comentarioService.getById(id);
        return assembler.toModel(comentario);
    }

    @GetMapping("/pelicula/{peliculaId}")
    public CollectionModel<EntityModel<ComentarioDTO>> obtenerComentariosPorPelicula(@PathVariable Long peliculaId) {
        logger.info("V2 GET /comentarios/pelicula/{} - Obteniendo comentarios por película", peliculaId);
        List<EntityModel<ComentarioDTO>> comentarios = comentarioService.getByPelicula(peliculaId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(comentarios,
                linkTo(methodOn(ComentarioControllerV2.class).obtenerComentariosPorPelicula(peliculaId)).withSelfRel(),
                linkTo(methodOn(ComentarioControllerV2.class).listarComentarios()).withRel("comentarios"));
    }

    @GetMapping("/usuario/{usuarioId}")
    public CollectionModel<EntityModel<ComentarioDTO>> obtenerComentariosPorUsuario(@PathVariable Long usuarioId) {
        logger.info("V2 GET /comentarios/usuario/{} - Obteniendo comentarios por usuario", usuarioId);
        List<EntityModel<ComentarioDTO>> comentarios = comentarioService.getByUsuario(usuarioId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(comentarios,
                linkTo(methodOn(ComentarioControllerV2.class).obtenerComentariosPorUsuario(usuarioId)).withSelfRel(),
                linkTo(methodOn(ComentarioControllerV2.class).listarComentarios()).withRel("comentarios"));
    }
}
