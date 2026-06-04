package com.peliculas.dto;

import com.peliculas.model.Genero;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneroDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    public Genero toModel() {
        Genero g = new Genero();
        g.setNombre(nombre);
        return g;
    }

    public static GeneroDTO fromModel(Genero g) {
        if (g == null) return null;
        return GeneroDTO.builder()
            .id(g.getId())
            .nombre(g.getNombre())
            .build();
    }
}