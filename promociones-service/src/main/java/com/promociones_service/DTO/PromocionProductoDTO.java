package com.promociones_service.DTO;


import com.promociones_service.model.PromocionProducto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromocionProductoDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    private String productoNombre;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    public PromocionProducto toModel() {
        PromocionProducto p = new PromocionProducto();
        p.setProductoId(productoId);
        p.setProductoNombre(productoNombre);
        p.setCantidad(cantidad);
        return p;
    }

    public static PromocionProductoDTO fromModel(PromocionProducto p) {
        if (p == null) return null;

        return PromocionProductoDTO.builder()
                .productoId(p.getProductoId())
                .productoNombre(p.getProductoNombre())
                .cantidad(p.getCantidad())
                .build();
    }
}
