package com.promociones_service.model;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Entity
@Table(name = "descuentos_categoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    /**
     * Debe coincidir con los valores del producto-service:
     * PALOMITAS, BEBIDAS, PAPAS_FRITAS, NACHOS, DULCES, COMBOS, OTRO
     */
    @Column(nullable = false, unique = true, length = 50)
    private String categoria;
 
    /**
     * Monto fijo que se descuenta del precioPromocion cuando
     * todos los productos de la promoción pertenecen a esta categoría.
     * Ej: 500.00 => se restan $500 al precio final.
     */
    @Column(name = "monto_descuento", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoDescuento;
}
