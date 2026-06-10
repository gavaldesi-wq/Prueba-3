package com.pagos.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoletaDTO {
    private Long pagoId;
    private Long reservaId;
    private String peliculaTitulo;
    private Long funcionId;
    private Integer cantidadEntradas;
    private Double totalEntradas;
    private Double totalProductos;
    private Double totalGeneral;
    private String metodoPago;
    private String estado;
    private Double subtotal;    
    private Double iva;         
    private Double totalConIva;

    private List<ProductoBoletaDTO> productos;

}
