package com.pagos.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.pagos.controller.PagosControllerV2;
import com.pagos.dto.PagosDTO;

@Component
public class PagosModelAssembler implements RepresentationModelAssembler<PagosDTO, EntityModel<PagosDTO>> {

    @Override
    public EntityModel<PagosDTO> toModel(PagosDTO pago) {
        return EntityModel.of(pago,
                linkTo(methodOn(PagosControllerV2.class).obtenerPago(pago.getId())).withSelfRel(),
                linkTo(methodOn(PagosControllerV2.class).listarPagos()).withRel("pagos"));
    }
}
