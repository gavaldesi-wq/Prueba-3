package com.promociones_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.promociones_service.controller.ComboControllerV2;
import com.promociones_service.DTO.ComboDTO;

@Component
public class ComboModelAssembler implements RepresentationModelAssembler<ComboDTO, EntityModel<ComboDTO>> {

    @Override
    public EntityModel<ComboDTO> toModel(ComboDTO combo) {
        return EntityModel.of(combo,
                linkTo(methodOn(ComboControllerV2.class).obtenerCombo(combo.getId())).withSelfRel(),
                linkTo(methodOn(ComboControllerV2.class).listarCombos()).withRel("combos"));
    }
}
