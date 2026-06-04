package com.sala.dto;
import com.sala.model.Sala;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

public class SalaDTO {

    
    private Long id;

    @NotBlank(message= "El nombre no puede estar vacio")
    private String nombre;
    @NotNull(message="Tiene que ingresar una capacidad")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    @Max(value = 500, message = "La capacidad no puede exceder 500")
    private Integer capacidad;

    @NotBlank(message="Tiene que ingresar el tipo de sala")
    @Pattern(regexp = "(?i)(2d|3d|imax|4d|4dx)", message = "Tipo de sala inválido. Valores permitidos: 2D, 3D, IMAX, 4D, 4DX")
    private String tipoSala;


    public Sala toModel(){

        Sala s = new Sala();
        s.setNombre(nombre);
        s.setCapacidad(capacidad);
        s.setTipoSala(tipoSala);
        return s;
    }

    public static SalaDTO fromModel(Sala s){
        if (s == null) return null;

        return SalaDTO.builder()
            .id(s.getId())
            .nombre(s.getNombre())
            .capacidad(s.getCapacidad())
            .tipoSala(s.getTipoSala())
            .build();

    }


}
