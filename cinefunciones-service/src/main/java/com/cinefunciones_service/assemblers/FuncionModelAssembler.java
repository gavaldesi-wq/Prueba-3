package com.cinefunciones_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.cinefunciones_service.controller.FuncionControllerV2;
import com.cinefunciones_service.dto.FuncionDTO;

@Component
public class FuncionModelAssembler implements RepresentationModelAssembler<FuncionDTO, EntityModel<FuncionDTO>> {

    @Override
    public EntityModel<FuncionDTO> toModel(FuncionDTO funcion) {
        return EntityModel.of(funcion,
                linkTo(methodOn(FuncionControllerV2.class).obtenerFuncion(funcion.getId())).withSelfRel(),
                linkTo(methodOn(FuncionControllerV2.class).listarFunciones()).withRel("funciones"));
    }
}
