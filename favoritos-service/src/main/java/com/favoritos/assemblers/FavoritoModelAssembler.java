package com.favoritos.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.favoritos.controller.FavoritoControllerV2;
import com.favoritos.dto.FavoritoDTO;

@Component
public class FavoritoModelAssembler implements RepresentationModelAssembler<FavoritoDTO, EntityModel<FavoritoDTO>> {

    @Override
    public EntityModel<FavoritoDTO> toModel(FavoritoDTO favorito) {
        return EntityModel.of(favorito,
                linkTo(methodOn(FavoritoControllerV2.class).obtenerFavorito(favorito.getId())).withSelfRel(),
                linkTo(methodOn(FavoritoControllerV2.class).listarFavoritos()).withRel("favoritos"));
    }
}
