package com.usuarios.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usuarios.model.Usuarios;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {

    
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String correo;
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private Long rolId; 

   
    /*Aca hacemos esto para convertir los datos que envia el usuario (DTO) a una entidad para poder guardarlos en la base de datos, esto es pal put o post*/
    public Usuarios toModel() {
        Usuarios u = new Usuarios(); 
        u.setId(id);
        u.setNombre(nombre);
        u.setCorreo(correo);
        u.setPassword(password);
       
        return u;
    }

   
    /*Esto es para cambiar la entidad a una dto, sirve para agarrar los datos de la entidad y transformarlos en dto para mostrar lo necesario en el get */
    public static UsuarioDTO fromModel(Usuarios u) {
        if (u == null) return null; /*con esto tamos confirmando de que el objeto venga vacio o no, osea si viene vacio se convierte a null y si no, hacemos el return */


        return UsuarioDTO.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .correo(u.getCorreo())
                .rolId(u.getRol() != null ? u.getRol().getId() : null) /*aca estamos verificando si el usuario tiene rol, si lo tiene devuelve el id del rol, si no, devuelve null */
                .build();

                /*Aca lo que se hace es empezar a crear el dto, vamos agarrando los datos que queremos de la entidad y luego los mostramos en el get */
    }
}