package com.pagos.service;


import com.pagos.dto.BoletaDTO;
import com.pagos.dto.CrearPagoRequestDTO;
import com.pagos.dto.PagosDTO;
import com.pagos.model.Pagos;
import com.pagos.Repository.PagosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagosService {

    private final PagosRepository pagosRepository;
    private final RestTemplate restTemplate;
    

    public List<PagosDTO> getAll() {

        return pagosRepository.findAll()
                .stream()
                .map(PagosDTO::fromModel)
                .collect(Collectors.toList());
    }
    public PagosDTO getById(Long id) {

        Pagos pago = pagosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        return PagosDTO.fromModel(pago);
    }

    public List<PagosDTO> getByReservaId(Long reservaId) {

        return pagosRepository.findByReservaId(reservaId)
                .stream()
                .map(PagosDTO::fromModel)
                .collect(Collectors.toList());
    }


    public BoletaDTO pagarReserva(CrearPagoRequestDTO request) {

        Map<String, Object> reserva = obtenerReserva(request.getReservaId());

        String peliculaTitulo = (String) reserva.get("peliculaTitulo");
        Long funcionId = Long.valueOf(reserva.get("funcionId").toString());
        Integer cantidadEntradas = Integer.valueOf(reserva.get("cantidadEntradas").toString());
        Double totalEntradas = Double.valueOf(reserva.get("totalEntradas").toString());
        Double totalProductos = Double.valueOf(reserva.get("totalProductos").toString());
        Double totalGeneral = Double.valueOf(reserva.get("totalGeneral").toString());
        Double iva = totalGeneral * 0.19;
        Double totalConIva = totalGeneral + iva;

        Pagos pago = new Pagos();
        pago.setReservaId(request.getReservaId());
        pago.setMonto(totalConIva);
        pago.setMetodoPago(request.getMetodoPago());
        pago.setEstado("PAGADO");

        Pagos guardado = pagosRepository.save(pago);

        return BoletaDTO.builder()
        .pagoId(guardado.getId())
        .reservaId(request.getReservaId())
        .peliculaTitulo(peliculaTitulo)
        .funcionId(funcionId)
        .cantidadEntradas(cantidadEntradas)
        .totalEntradas(totalEntradas)
        .totalProductos(totalProductos)
        .subtotal(totalGeneral)
        .iva(iva)
        .totalConIva(totalConIva)
        .metodoPago(request.getMetodoPago())
        .estado("PAGADO")
        .build();
    }


    public PagosDTO update(Long id, PagosDTO dto) {

        Pagos pago = pagosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        pago.setReservaId(dto.getReservaId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstado(dto.getEstado());

        Pagos actualizado = pagosRepository.save(pago);

        return PagosDTO.fromModel(actualizado);
    }

  
    public void delete(Long id) {
        Pagos pago = pagosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        pagosRepository.delete(pago);
    }

    
    private Map<String, Object> obtenerReserva(Long reservaId) {
        try {
            String url =
                    "http://reservas-service:8085/api/reservas/" + reservaId;

            return restTemplate.getForObject(url,Map.class);

        } catch (RestClientException e) {
            throw new RuntimeException(
                    "La reserva con ID "
                            + reservaId
                            + " no existe"
            );
        }
    }
}