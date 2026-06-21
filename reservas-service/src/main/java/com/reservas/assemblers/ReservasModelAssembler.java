package com.reservas.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.reservas.controller.ReservasControllerV2;
import com.reservas.dto.ReservasDTO;

@Component
public class ReservasModelAssembler implements RepresentationModelAssembler<ReservasDTO, EntityModel<ReservasDTO>> {

    @Override
    public EntityModel<ReservasDTO> toModel(ReservasDTO reserva) {
        return EntityModel.of(reserva,
                linkTo(methodOn(ReservasControllerV2.class).obtenerReserva(reserva.getId())).withSelfRel(),
                linkTo(methodOn(ReservasControllerV2.class).listarReservas()).withRel("reservas"));
    }
}
