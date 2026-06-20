package com.comentarios.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.comentarios.controller.ComentarioControllerV2;
import com.comentarios.dto.ComentarioDTO;

@Component
public class ComentarioModelAssembler implements RepresentationModelAssembler<ComentarioDTO, EntityModel<ComentarioDTO>> {

    @Override
    public EntityModel<ComentarioDTO> toModel(ComentarioDTO comentario) {
        return EntityModel.of(comentario,
                linkTo(methodOn(ComentarioControllerV2.class).obtenerComentario(comentario.getId())).withSelfRel(),
                linkTo(methodOn(ComentarioControllerV2.class).listarComentarios()).withRel("comentarios"));
    }
}
