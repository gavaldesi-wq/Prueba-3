package com.comentarios.dto;

import java.time.LocalDateTime;

import com.comentarios.model.Comentario;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComentarioDTO {
   private Long id;

    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotNull(message = "El ID de la película es obligatorio")
    private Long peliculaId;

    @NotBlank(message = "El contenido del comentario es obligatorio")
    @Size(min = 1, max = 500, message = "El comentario debe tener entre 1 y 500 caracteres")
    private String contenido;

    // Campos de solo lectura (los rellena el service)
    private Long usuarioId;
    private String usuarioNombre;
    private String peliculaTitulo;
    private LocalDateTime fechaCreacion;

    public Comentario toModel() {
        Comentario c = new Comentario();
        c.setId(id);
        c.setPeliculaId(peliculaId);
        c.setContenido(contenido);
        c.setFechaCreacion(LocalDateTime.now());
        return c;
    }

    public static ComentarioDTO fromModel(Comentario c) {
        if (c == null) return null;
        return ComentarioDTO.builder()
                .id(c.getId())
                .usuarioId(c.getUsuarioId())
                .usuarioNombre(c.getUsuarioNombre())
                .peliculaId(c.getPeliculaId())
                .peliculaTitulo(c.getPeliculaTitulo())
                .contenido(c.getContenido())
                .fechaCreacion(c.getFechaCreacion())
                .build();
    }
}
