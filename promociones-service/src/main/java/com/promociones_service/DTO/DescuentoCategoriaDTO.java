package com.promociones_service.DTO;


import com.promociones_service.model.DescuentoCategoria;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
import java.math.BigDecimal;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DescuentoCategoriaDTO {
    private Long id;
 
    @NotBlank(message = "La categoría es obligatoria")
    @Pattern(
        regexp = "(?i)(PALOMITAS|BEBIDAS|PAPAS_FRITAS|NACHOS|DULCES|COMBOS|OTRO)",
        message = "Categoría inválida. Valores permitidos: PALOMITAS, BEBIDAS, PAPAS_FRITAS, NACHOS, DULCES, COMBOS, OTRO"
    )
    private String categoria;
 
    @NotNull(message = "El monto de descuento es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto de descuento debe ser mayor a 0")
    private BigDecimal montoDescuento;
 
    public DescuentoCategoria toModel() {
        DescuentoCategoria d = new DescuentoCategoria();
        d.setId(id);
        d.setCategoria(categoria != null ? categoria.toUpperCase() : null);
        d.setMontoDescuento(montoDescuento);
        return d;
    }
 
    public static DescuentoCategoriaDTO fromModel(DescuentoCategoria d) {
        if (d == null) return null;
        return DescuentoCategoriaDTO.builder()
                .id(d.getId())
                .categoria(d.getCategoria())
                .montoDescuento(d.getMontoDescuento())
                .build();
    }
}
