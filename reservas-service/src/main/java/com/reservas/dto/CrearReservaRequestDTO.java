package com.reservas.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearReservaRequestDTO {
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotNull(message = "El ID de la función es obligatorio")
    private Long funcionId;

    @NotNull(message = "La cantidad de entradas es obligatoria")
    @Min(value = 1, message = "Debe comprar al menos 1 entrada")
    private Integer cantidadEntradas;
}

