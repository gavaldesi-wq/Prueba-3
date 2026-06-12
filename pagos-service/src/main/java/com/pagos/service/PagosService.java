package com.pagos.service;


import com.pagos.dto.BoletaDTO;
import com.pagos.dto.CrearPagoRequestDTO;
import com.pagos.dto.PagosDTO;
import com.pagos.dto.ProductoBoletaDTO;
import com.pagos.model.Pagos;
import com.pagos.Repository.PagosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagosService {

    private final PagosRepository pagosRepository;
    private final RestTemplate restTemplate;
    

    public List<PagosDTO> getAll() {
        log.info("Obteniendo todos los pagos");

        return pagosRepository.findAll()
                .stream()
                .map(PagosDTO::fromModel)
                .collect(Collectors.toList());
    }
    public PagosDTO getById(Long id) {
        log.info("Buscando pago id={}", id);

        Pagos pago = pagosRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado id={}", id);
                    return new RuntimeException("Pago no encontrado");
                });

        log.info("Pago encontrado id={}", id);
        return PagosDTO.fromModel(pago);
    }

    public List<PagosDTO> getByReservaId(Long reservaId) {
        log.info("Obteniendo pagos por reserva id={}", reservaId);

        return pagosRepository.findByReservaId(reservaId)
                .stream()
                .map(PagosDTO::fromModel)
                .collect(Collectors.toList());
    }


    public BoletaDTO pagarReserva(CrearPagoRequestDTO request) {
        log.info("Procesando pago para reservaId={}", request.getReservaId());

        Map<String, Object> reserva = obtenerReserva(request.getReservaId());

        List<Map<String, Object>> productosMap = (List<Map<String, Object>>) reserva.get("productos");

        List<ProductoBoletaDTO> productos = new ArrayList<>();

        if (productosMap != null) {

        for (Map<String, Object> p : productosMap) {

        ProductoBoletaDTO producto = new ProductoBoletaDTO();
        producto.setNombre((String) p.get("nombre"));
        producto.setCantidad(
                Integer.valueOf(p.get("cantidad").toString())
        );
        producto.setPrecioUnitario(
                Double.valueOf(p.get("precioUnitario").toString())
        );
        producto.setSubtotal(
                Double.valueOf(p.get("subtotal").toString())
        );
        productos.add(producto);
    }
}
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

        log.info("Pago creado exitosamente id={}", guardado.getId());

        return BoletaDTO.builder()
        .pagoId(guardado.getId())
        .reservaId(request.getReservaId())
        .peliculaTitulo(peliculaTitulo)
        .funcionId(funcionId)
        .cantidadEntradas(cantidadEntradas)
        .totalEntradas(totalEntradas)
        .totalProductos(totalProductos)
        .totalGeneral(totalGeneral)
        .productos(productos)
        .subtotal(totalGeneral)
        .iva(iva)
        .totalConIva(totalConIva)
        .metodoPago(request.getMetodoPago())
        .estado("PAGADO")
        .build();
    }


    public PagosDTO update(Long id, PagosDTO dto) {
        log.info("Actualizando pago id={}", id);

        Pagos pago = pagosRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado id={}", id);
                    return new RuntimeException("Pago no encontrado");
                });

        pago.setReservaId(dto.getReservaId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstado(dto.getEstado());

        Pagos actualizado = pagosRepository.save(pago);

        log.info("Pago actualizado exitosamente id={}", id);
        return PagosDTO.fromModel(actualizado);
    }

  
    public void delete(Long id) {
        log.info("Eliminando pago id={}", id);
        Pagos pago = pagosRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado id={}", id);
                    return new RuntimeException("Pago no encontrado");
                });
        pagosRepository.delete(pago);
        log.info("Pago eliminado exitosamente id={}", id);
    }

    
    private Map<String, Object> obtenerReserva(Long reservaId) {
        try {
            String url =
                    "http://reservas-service:8085/api/reservas/" + reservaId;

            return restTemplate.getForObject(url,Map.class);

        } catch (RestClientException e) {
            log.warn("Reserva no encontrada id={}", reservaId);
            throw new RuntimeException(
                    "La reserva con ID "
                            + reservaId
                            + " no existe"
            );
        }
    }
}