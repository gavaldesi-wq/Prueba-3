package com.promociones_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoComboDTO{
    
    private String nombre;
    private Integer cantidad;
}