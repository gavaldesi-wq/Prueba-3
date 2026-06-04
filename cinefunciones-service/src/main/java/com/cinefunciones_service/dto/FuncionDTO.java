package com.cinefunciones_service.dto;

import com.cinefunciones_service.model.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FuncionDTO {

    private Long id;

    @NotNull(message = "Tiene que ingresar el ID de la película")
    private Long peliculaId;
    private String peliculaTitulo; 

    @NotNull(message = "Tiene que ingresar el ID de la sala")
    private Long salaId;
    private String salaNombre; 
    private String salaTipo;

    @NotNull(message = "Tiene que ingresar la fecha")
    private LocalDate fecha;

    @NotNull(message = "Tiene que ingresar la hora de inicio")
    private LocalTime horaInicio;

    @NotNull(message = "Tiene que ingresar la hora de fin")
    private LocalTime horaFin;

    @NotNull(message = "Tiene que ingresar el precio general")
    @DecimalMin(value = "0.01", message = "El precio general debe ser mayor a 0")
    private BigDecimal precioGeneral;

    @DecimalMin(value = "0.00", message = "El precio VIP no puede ser negativo")
    private BigDecimal precioVip;

    @NotBlank(message = "Tiene que ingresar el estado")
    @Pattern(regexp = "(?i)(DISPONIBLE|AGOTADA|CANCELADA)", 
             message = "Estado inválido. Valores permitidos: DISPONIBLE, AGOTADA, CANCELADA")
    private String estado;

    @NotBlank(message = "Tiene que ingresar el idioma")
    @Pattern(regexp = "(?i)(ESPAÑOL|INGLÉS|SUBTITULADA)", 
             message = "Idioma inválido. Valores permitidos: ESPAÑOL, INGLÉS, SUBTITULADA")
    private String idioma;

    @NotBlank(message = "Tiene que ingresar el formato")
    @Pattern(regexp = "(?i)(2D|3D|IMAX|4D|4DX|VIP)", 
             message = "Formato inválido. Valores permitidos: 2D, 3D, IMAX, 4D, 4DX, VIP")
    private String formato;



    public FuncionModel toModel() {
        FuncionModel f = new FuncionModel();
        f.setPeliculaId(peliculaId);
        f.setPeliculaTitulo(peliculaTitulo); 
        f.setSalaId(salaId);
        f.setSalaNombre(salaNombre); 
        f.setSalaTipo(salaTipo != null ? salaTipo.toUpperCase() : null); 
        f.setFecha(fecha);
        f.setHoraInicio(horaInicio);
        f.setHoraFin(horaFin);
        f.setPrecioGeneral(precioGeneral);
        f.setPrecioVip(precioVip);
        f.setEstado(estado != null ? estado.toUpperCase() : null);
        f.setIdioma(idioma != null ? idioma.toUpperCase() : null);
        f.setFormato(formato != null ? formato.toUpperCase() : null);
        return f;
    }

    public static FuncionDTO fromModel(FuncionModel f) {
        if (f == null) return null;

        return FuncionDTO.builder()
            .id(f.getId())
            .peliculaId(f.getPeliculaId())
            .peliculaTitulo(f.getPeliculaTitulo()) 
            .salaId(f.getSalaId())
            .salaNombre(f.getSalaNombre()) 
            .salaTipo(f.getSalaTipo()) 
            .fecha(f.getFecha())
            .horaInicio(f.getHoraInicio())
            .horaFin(f.getHoraFin())
            .precioGeneral(f.getPrecioGeneral())
            .precioVip(f.getPrecioVip())
            .estado(f.getEstado())
            .idioma(f.getIdioma())
            .formato(f.getFormato())
            .build();
    }

}