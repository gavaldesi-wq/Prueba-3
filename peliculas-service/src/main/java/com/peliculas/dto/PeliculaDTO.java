package com.peliculas.dto;

import com.peliculas.model.Pelicula;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeliculaDTO {

    private Long id;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "La clasificación es obligatoria")
    @Pattern(regexp = "(?i)(G|PG|PG-13|R|NC-17|NR)",
             message = "Clasificación inválida. Valores permitidos: G, PG, PG-13, R, NC-17, NR")
    private String clasificacion;

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser mayor a 0")
    private Integer duracion;

    @NotNull(message = "La fecha de estreno es obligatoria")
    private LocalDate fechaEstreno;

    @NotNull(message = "El ID del género es obligatorio")
    private Long generoId;

    private String generoNombre;

    public Pelicula toModel() {
        Pelicula p = new Pelicula();
        p.setTitulo(titulo);
        p.setClasificacion(clasificacion);
        p.setDuracion(duracion);
        p.setFechaEstreno(fechaEstreno);
        return p;
    }

    public static PeliculaDTO fromModel(Pelicula p) {
        if (p == null) return null;
        return PeliculaDTO.builder()
            .id(p.getId())
            .titulo(p.getTitulo())
            .clasificacion(p.getClasificacion())
            .duracion(p.getDuracion())
            .fechaEstreno(p.getFechaEstreno())
            .generoId(p.getGenero() != null ? p.getGenero().getId() : null)
            .generoNombre(p.getGenero() != null ? p.getGenero().getNombre() : null)
            .build();
    }
}