package com.pagos.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoBoletaDTO {
    private String nombre;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
