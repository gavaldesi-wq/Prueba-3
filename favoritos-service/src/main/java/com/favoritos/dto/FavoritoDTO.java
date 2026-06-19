package com.favoritos.dto;
 
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.favoritos.model.Favorito;

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
public class FavoritoDTO {
 
    private Long id;
@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "El correo es obligatorio")
    private String correo;
@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
 
    @NotNull(message = "El ID de la película es obligatorio")
    private Long peliculaId;
 
    // Campos de solo lectura (los rellena el service)
    private Long usuarioId;
    private String usuarioNombre;
    private String peliculaTitulo;
    private LocalDateTime fechaAgregado;
 
    public Favorito toModel() {
        Favorito f = new Favorito();
        f.setId(id);
        f.setPeliculaId(peliculaId);
        f.setFechaAgregado(LocalDateTime.now());
        return f;
    }
 
    public static FavoritoDTO fromModel(Favorito f) {
        if (f == null) return null;
        return FavoritoDTO.builder()
                .id(f.getId())
                .usuarioId(f.getUsuarioId())
                .usuarioNombre(f.getUsuarioNombre())
                .peliculaId(f.getPeliculaId())
                .peliculaTitulo(f.getPeliculaTitulo())
                .fechaAgregado(f.getFechaAgregado())
                .build();
    }
}
