package com.sala.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.sala.controller.SalaControllerV2;
import com.sala.dto.SalaDTO;

@Component
public class SalaModelAssembler implements RepresentationModelAssembler<SalaDTO, EntityModel<SalaDTO>> {

    @Override
    public EntityModel<SalaDTO> toModel(SalaDTO sala) {
        return EntityModel.of(sala,
                linkTo(methodOn(SalaControllerV2.class).obtenerSala(sala.getId())).withSelfRel(),
                linkTo(methodOn(SalaControllerV2.class).listarSalas(null)).withRel("salas"));
    }


}
