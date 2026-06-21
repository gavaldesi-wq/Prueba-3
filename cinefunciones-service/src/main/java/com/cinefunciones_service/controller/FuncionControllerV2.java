package com.cinefunciones_service.controller;

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
import com.cinefunciones_service.assemblers.FuncionModelAssembler;
import com.cinefunciones_service.dto.FuncionDTO;
import com.cinefunciones_service.service.FuncionService;

@RestController
@RequestMapping("funciones/v2")
public class FuncionControllerV2 {
    private final FuncionService funcionService;
    private final FuncionModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(FuncionControllerV2.class);

    public FuncionControllerV2(FuncionService funcionService, FuncionModelAssembler assembler) {
        this.funcionService = funcionService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<FuncionDTO>> listarFunciones() {
        logger.info("V2 GET /funciones - Listando funciones");
        List<EntityModel<FuncionDTO>> funciones = funcionService.getAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(funciones, linkTo(methodOn(FuncionControllerV2.class).listarFunciones()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<FuncionDTO> obtenerFuncion(@PathVariable Long id) {
        logger.info("V2 GET /funciones/{} - Obteniendo función", id);
        FuncionDTO funcion = funcionService.getById(id);
        return assembler.toModel(funcion);
    }

    @GetMapping("/pelicula/{peliculaId}")
    public CollectionModel<EntityModel<FuncionDTO>> obtenerFuncionesPorPelicula(@PathVariable Long peliculaId) {
        logger.info("V2 GET /funciones/pelicula/{} - Obteniendo funciones por película", peliculaId);
        List<EntityModel<FuncionDTO>> funciones = funcionService.getByPelicula(peliculaId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(funciones, linkTo(methodOn(FuncionControllerV2.class).obtenerFuncionesPorPelicula(peliculaId)).withSelfRel());
    }

    @GetMapping("/sala/{salaId}")
    public CollectionModel<EntityModel<FuncionDTO>> obtenerFuncionesPorSala(@PathVariable Long salaId) {
        logger.info("V2 GET /funciones/sala/{} - Obteniendo funciones por sala", salaId);
        List<EntityModel<FuncionDTO>> funciones = funcionService.getBySala(salaId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(funciones, linkTo(methodOn(FuncionControllerV2.class).obtenerFuncionesPorSala(salaId)).withSelfRel());
    }

    @GetMapping("/fecha")
    public CollectionModel<EntityModel<FuncionDTO>> obtenerFuncionesPorFecha(@RequestParam String fecha) {
        logger.info("V2 GET /funciones/fecha - Obteniendo funciones por fecha={}", fecha);
        List<EntityModel<FuncionDTO>> funciones = funcionService.getByFecha(fecha).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(funciones, linkTo(methodOn(FuncionControllerV2.class).obtenerFuncionesPorFecha(fecha)).withSelfRel());
    }

    @GetMapping("/estado")
    public CollectionModel<EntityModel<FuncionDTO>> obtenerFuncionesPorEstado(@RequestParam String estado) {
        logger.info("V2 GET /funciones/estado - Obteniendo funciones por estado={}", estado);
        List<EntityModel<FuncionDTO>> funciones = funcionService.getByEstado(estado).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(funciones, linkTo(methodOn(FuncionControllerV2.class).obtenerFuncionesPorEstado(estado)).withSelfRel());
    }

    @GetMapping("/disponibles")
    public CollectionModel<EntityModel<FuncionDTO>> obtenerFuncionesDisponibles() {
        logger.info("V2 GET /funciones/disponibles - Obteniendo funciones disponibles");
        List<EntityModel<FuncionDTO>> funciones = funcionService.getDisponibles().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(funciones, linkTo(methodOn(FuncionControllerV2.class).obtenerFuncionesDisponibles()).withSelfRel());
    }

    @GetMapping("/formato")
    public CollectionModel<EntityModel<FuncionDTO>> obtenerFuncionesPorFormato(@RequestParam String formato) {
        logger.info("V2 GET /funciones/formato - Obteniendo funciones por formato={}", formato);
        List<EntityModel<FuncionDTO>> funciones = funcionService.getByFormato(formato).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(funciones, linkTo(methodOn(FuncionControllerV2.class).obtenerFuncionesPorFormato(formato)).withSelfRel());
    }

    @GetMapping("/idioma")
    public CollectionModel<EntityModel<FuncionDTO>> obtenerFuncionesPorIdioma(@RequestParam String idioma) {
        logger.info("V2 GET /funciones/idioma - Obteniendo funciones por idioma={}", idioma);
        List<EntityModel<FuncionDTO>> funciones = funcionService.getByIdioma(idioma).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(funciones, linkTo(methodOn(FuncionControllerV2.class).obtenerFuncionesPorIdioma(idioma)).withSelfRel());
    }
}
