package com.usuarios.controller;

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
import com.usuarios.assemblers.UsuarioModelAssembler;
import com.usuarios.dto.UsuarioDTO;
import com.usuarios.service.UsuarioService;

@RestController
@RequestMapping("usuarios/v2")
public class UsuarioControllerV2 {
    private final UsuarioService usuarioService;
    private final UsuarioModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(UsuarioControllerV2.class);

    public UsuarioControllerV2(UsuarioService usuarioService, UsuarioModelAssembler assembler) {
        this.usuarioService = usuarioService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<UsuarioDTO>> listarUsuarios() {
        logger.info("V2 GET /usuarios - Listando usuarios");
        List<EntityModel<UsuarioDTO>> usuarios = usuarioService.getAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(usuarios, linkTo(methodOn(UsuarioControllerV2.class).listarUsuarios()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<UsuarioDTO> obtenerUsuario(@PathVariable Long id) {
        logger.info("V2 GET /usuarios/{} - Obteniendo usuario", id);
        UsuarioDTO usuario = usuarioService.getById(id);
        return assembler.toModel(usuario);
    }
}

