
package com.producto.DTO;

import com.producto.model.Producto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProductoDTO {

    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    @NotNull(message = "El precio del producto es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotBlank(message = "La categoría del producto es obligatoria")
    @Pattern(
        regexp = "(?i)(PALOMITAS|BEBIDAS|PAPAS_FRITAS|NACHOS|DULCES|COMBOS|OTRO)",
        message = "Categoría inválida. Valores permitidos: PALOMITAS, BEBIDAS, PAPAS_FRITAS, NACHOS, DULCES, COMBOS, OTRO"
    )
    private String categoria;

    public Producto toModel() {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setCategoria(categoria != null ? categoria.toUpperCase() : null);
        return p;
    }

    public static ProductoDTO fromModel(Producto p) {
        if (p == null) return null;

        return ProductoDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .precio(p.getPrecio())
                .categoria(p.getCategoria())
                .build();
    }
}

