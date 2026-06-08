package com.reservas.dto;

import com.reservas.model.Reservas;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservasDTO {

    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "La funcion es obligatoria")
    private Long funcionId;

    private String peliculaTitulo;

    @NotNull(message = "La cantidad de entradas es obligatoria")
    @Min(value = 1, message = "La cantidad de entradas debe ser al menos 1")
    private Integer cantidadEntradas;

    @NotNull(message = "El estado es obligatorio")
    private String estado;

    private List<ProductoReservaDTO> productos;

    private Double totalProductos;
    private Double totalEntradas;
    private Double totalGeneral;

    public Reservas toModel() {
        Reservas r = new Reservas();
        r.setUsuarioId(usuarioId);
        r.setFuncionId(funcionId);
        r.setPeliculaTitulo(peliculaTitulo);
        r.setCantidadEntradas(cantidadEntradas);
        r.setEstado(estado);
        r.setTotalProductos(totalProductos != null ? totalProductos : 0.0);
        r.setTotalEntradas(totalEntradas != null ? totalEntradas : 0.0);
        r.setTotalGeneral(totalGeneral != null ? totalGeneral : 0.0);
        return r;
    }

    public static ReservasDTO fromModel(Reservas r) {
        if (r == null)
            return null;

        return ReservasDTO.builder()
                .id(r.getId())
                .usuarioId(r.getUsuarioId())
                .funcionId(r.getFuncionId())
                .peliculaTitulo(r.getPeliculaTitulo())
                .cantidadEntradas(r.getCantidadEntradas())
                .estado(r.getEstado())
                .totalProductos(r.getTotalProductos())
                .totalEntradas(r.getTotalEntradas())
                .totalGeneral(r.getTotalGeneral())
                .build();
    }
}