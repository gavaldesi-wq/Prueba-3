package com.promociones_service.DTO;

 
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.promociones_service.model.Promocion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class PromocionDTO {

    private Long id;
 
    @NotBlank(message = "El nombre de la promoción es obligatorio")
    private String nombre;
 
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
 
    @NotNull(message = "El precio de la promoción es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precioPromocion;
 
    /**
     * Precio final después de aplicar el descuento por categoría.
     * Es de solo lectura: se calcula en el servicio y nunca viene del cliente.
     */
    private BigDecimal precioFinal;
 
    /**
     * Nombre de la categoría que generó el descuento, para informar al cliente.
     * Ej: "BEBIDAS". Null si no aplica ningún descuento.
     */
    private String categoriaDescuento;
 
    /**
     * Monto de descuento aplicado. Null si no aplica ningún descuento.
     */
    private BigDecimal montoDescuentoAplicado;
 
    @NotBlank(message = "El estado es obligatorio")
    @Pattern(
            regexp = "(?i)(ACTIVA|INACTIVA|VENCIDA)",
            message = "Estado inválido. Valores permitidos: ACTIVA, INACTIVA, VENCIDA"
    )
    private String estado;
 
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;
 
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;
 
    @Valid
    @NotEmpty(message = "La promoción debe tener al menos un producto")
    private List<PromocionProductoDTO> productos;
 
    public Promocion toModel() {
        Promocion p = new Promocion();
        p.setId(id);
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecioPromocion(precioPromocion);
        p.setEstado(estado != null ? estado.toUpperCase() : null);
        p.setFechaInicio(fechaInicio);
        p.setFechaFin(fechaFin);
        return p;
    }
 
    public static PromocionDTO fromModel(Promocion p) {
        if (p == null) return null;
 
        return PromocionDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .precioPromocion(p.getPrecioPromocion())
                // precioFinal, categoriaDescuento y montoDescuentoAplicado
                // son calculados por PromocionService y seteados externamente
                .estado(p.getEstado())
                .fechaInicio(p.getFechaInicio())
                .fechaFin(p.getFechaFin())
                .productos(
                        p.getProductos() != null
                                ? p.getProductos().stream()
                                    .map(PromocionProductoDTO::fromModel)
                                    .collect(Collectors.toList())
                                : null
                )
                .build();
    }
}
