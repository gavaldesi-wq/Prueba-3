package com.pagos.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearPagoRequestDTO {

    @NotNull(message = "La reserva es obligatoria")
    private Long reservaId;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;

}
