package com.pagos.dto;
import com.pagos.model.Pagos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagosDTO {
    private Long id;

    @NotNull(message = "La reserva es obligatoria")
    private Long reservaId;

    @NotNull(message = "El monto es obligatorio")
    @Min(value = 1, message = "El monto debe ser mayor a 0")
    private Double monto;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    public Pagos toModel() {
        Pagos pago = new Pagos();

        pago.setReservaId(reservaId);
        pago.setMonto(monto);
        pago.setMetodoPago(metodoPago);
        pago.setEstado(estado);

        return pago;
    }


     public static PagosDTO fromModel(Pagos pago) {

        if (pago == null)
            return null;

        return PagosDTO.builder()
                .id(pago.getId())
                .reservaId(pago.getReservaId())
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago())
                .estado(pago.getEstado())
                .build();
    }
}
