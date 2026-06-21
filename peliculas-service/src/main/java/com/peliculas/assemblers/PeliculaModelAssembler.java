package com.peliculas.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.peliculas.controller.PeliculaControllerV2;
import com.peliculas.dto.PeliculaDTO;

@Component
public class PeliculaModelAssembler implements RepresentationModelAssembler<PeliculaDTO, EntityModel<PeliculaDTO>> {

    @Override
    public EntityModel<PeliculaDTO> toModel(PeliculaDTO pelicula) {
        return EntityModel.of(pelicula,
                linkTo(methodOn(PeliculaControllerV2.class).obtenerPelicula(pelicula.getId())).withSelfRel(),
                linkTo(methodOn(PeliculaControllerV2.class).listarPeliculas()).withRel("peliculas"));
    }
}
