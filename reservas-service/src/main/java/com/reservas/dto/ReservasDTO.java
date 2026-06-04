package com.reservas.dto;

import com.reservas.model.Reservas;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservasDTO {

    private Long id;
    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;
    @NotNull(message = "La funcion es obligatoria")
    private Long funcionId;
    @NotNull(message = "La cantidad de entradas es obligatoria")
    @Min(value = 1, message = "La cantidad de entradas debe ser al menos 1")
    private Integer cantidadEntradas;
    @NotNull(message = "El estado es obligatorio")
    private String estado;


    public Reservas toModel(){
        Reservas r = new Reservas();
        r.setUsuarioId(usuarioId);
        r.setFuncionId(funcionId);
        r.setCantidadEntradas(cantidadEntradas);
        r.setEstado(estado);
        return r;
    }
    public static ReservasDTO fromModel(Reservas r){

        if (r == null) 
            return null;

        return ReservasDTO.builder()
                .id(r.getId())
                .usuarioId(r.getUsuarioId())
                .funcionId(r.getFuncionId())
                .cantidadEntradas(r.getCantidadEntradas())
                .estado(r.getEstado())
                .build();
    }

}

