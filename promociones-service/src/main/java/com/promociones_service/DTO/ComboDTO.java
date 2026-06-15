package com.promociones_service.DTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.promociones_service.model.Combo;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComboDTO {

    private Long id;

    @NotBlank(message = "El nombre del combo es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripción del combo es obligatoria")
    private String descripcion;

    @NotNull(message = "El precio del combo es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio del combo debe ser mayor a 0")
    private Double precioCombo;

    @NotNull(message = "Los productos del combo son obligatorios")
    @Size(min = 2, message = "Un combo debe tener al menos 2 productos")
    private List<ProductoComboDTO> productos;

    private Boolean activo;

    public Combo toModel() {
        Combo c = new Combo();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setPrecioCombo(precioCombo);
        c.setActivo(activo != null ? activo : true);
        try {
            ObjectMapper mapper = new ObjectMapper();
            c.setProductosJson(mapper.writeValueAsString(productos));
        } catch (Exception e) {
            c.setProductosJson("[]");
        }
        return c;
    }

    public static ComboDTO fromModel(Combo c) {
        if (c == null) return null;

        List<ProductoComboDTO> productos = new ArrayList<>();
        try {
            if (c.getProductosJson() != null && !c.getProductosJson().isBlank()) {
                ObjectMapper mapper = new ObjectMapper();
                productos = mapper.readValue(
                        c.getProductosJson(),
                        new TypeReference<List<ProductoComboDTO>>() {}
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ComboDTO.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .descripcion(c.getDescripcion())
                .precioCombo(c.getPrecioCombo())
                .productos(productos)
                .activo(c.getActivo())
                .build();
    }
}