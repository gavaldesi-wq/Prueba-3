package com.pagos.service;

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


    public PagosDTO pagarReserva(CrearPagoRequestDTO request) {

        obtenerReserva(request.getReservaId());

        Pagos pago = new Pagos();

        pago.setReservaId(request.getReservaId());
        pago.setMonto(request.getMonto());
        pago.setMetodoPago(request.getMetodoPago());
        pago.setEstado("PAGADO");

        Pagos guardado = pagosRepository.save(pago);

        return PagosDTO.fromModel(guardado);
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
                    "http://localhost:8085/api/reservas/" + reservaId;

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