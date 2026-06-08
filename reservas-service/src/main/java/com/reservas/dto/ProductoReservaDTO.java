package com.reservas.dto;

import lombok.Data;

@Data
public class ProductoReservaDTO {
    private String nombre;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

}
