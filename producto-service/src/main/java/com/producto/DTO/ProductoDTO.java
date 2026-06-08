package com.producto.DTO;

import com.producto.model.Producto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Este sistema es usado para crear, actualizar y mostrar productos de comida en nuestro")

public class ProductoDTO {
@Schema(
        description = "ID único del producto",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Schema(
        description = "Nombre del producto",
        example = "Cabritas grandes",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombre;

    @NotNull(message = "El precio del producto es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Schema(
        description = "Precio del producto",
        example = "3500",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double precio;

    // Convierte un ProductoDTO a un Producto.
    // Se usa cuando los datos llegan desde la API y se quieren guardar en la base de datos.
    public Producto toModel() {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setPrecio(precio);
        return p;
    }

    // Convierte un Producto a ProductoDTO.
    // Se usa cuando los datos vienen desde la base de datos y se quieren devolver al cliente.
    public static ProductoDTO fromModel(Producto p) {
        if (p == null) return null;

        return ProductoDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .precio(p.getPrecio())
                .build();
    }
}

