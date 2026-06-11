package com.promociones_service.service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.promociones_service.DTO.PromocionDTO;
import com.promociones_service.DTO.PromocionProductoDTO;
import com.promociones_service.model.DescuentoCategoria;
import com.promociones_service.model.Promocion;
import com.promociones_service.model.PromocionProducto;
import com.promociones_service.repository.DescuentoCategoriaRepository;
import com.promociones_service.repository.PromocionRepository;

import lombok.RequiredArgsConstructor;
 
@Service
@RequiredArgsConstructor
public class PromocionService {

  
    private final PromocionRepository promocionRepository;
    private final DescuentoCategoriaRepository descuentoCategoriaRepository;
    private final RestTemplate restTemplate;
 
    public List<PromocionDTO> getAll() {
        return promocionRepository.findAll()
                .stream()
                .map(this::toDTOConDescuento)
                .collect(Collectors.toList());
    }
 
    public PromocionDTO getById(Long id) {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada"));
        return toDTOConDescuento(promocion);
    }
 
    public List<PromocionDTO> getActivas() {
        return promocionRepository.findByEstado("ACTIVA")
                .stream()
                .filter(this::estaDentroDeFechas)
                .map(this::toDTOConDescuento)
                .collect(Collectors.toList());
    }
 
    public PromocionDTO save(PromocionDTO dto) {
        validarFechas(dto);
 
        Promocion promocion = dto.toModel();
 
        List<PromocionProducto> productos = dto.getProductos()
                .stream()
                .map(productoDTO -> crearPromocionProducto(productoDTO, promocion))
                .collect(Collectors.toList());
 
        promocion.getProductos().clear();
        promocion.getProductos().addAll(productos);
 
        Promocion guardada = promocionRepository.save(promocion);
        return toDTOConDescuento(guardada);
    }
 
    public PromocionDTO update(Long id, PromocionDTO dto) {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada"));
 
        validarFechas(dto);
 
        promocion.setNombre(dto.getNombre());
        promocion.setDescripcion(dto.getDescripcion());
        promocion.setPrecioPromocion(dto.getPrecioPromocion());
        promocion.setEstado(dto.getEstado().toUpperCase());
        promocion.setFechaInicio(dto.getFechaInicio());
        promocion.setFechaFin(dto.getFechaFin());
 
        promocion.getProductos().clear();
 
        List<PromocionProducto> productos = dto.getProductos()
                .stream()
                .map(productoDTO -> crearPromocionProducto(productoDTO, promocion))
                .collect(Collectors.toList());
 
        promocion.getProductos().addAll(productos);
 
        Promocion actualizada = promocionRepository.save(promocion);
        return toDTOConDescuento(actualizada);
    }
 
    public void delete(Long id) {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada"));
        promocionRepository.delete(promocion);
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    // Lógica de descuento por categoría
    // ─────────────────────────────────────────────────────────────────────────
 
    /**
     * Convierte una Promocion a DTO y le aplica el descuento si corresponde.
     *
     * Regla: si TODOS los productos de la promoción pertenecen a la MISMA
     * categoría, y esa categoría tiene un descuento configurado, se resta
     * el monto fijo al precioPromocion para obtener el precioFinal.
     * Si no aplica ningún descuento, precioFinal == precioPromocion.
     */
    private PromocionDTO toDTOConDescuento(Promocion promocion) {
        PromocionDTO dto = PromocionDTO.fromModel(promocion);
 
        if (promocion.getProductos() == null || promocion.getProductos().isEmpty()) {
            dto.setPrecioFinal(promocion.getPrecioPromocion());
            return dto;
        }
 
        // Obtener la categoría de cada producto consultando producto-service
        List<String> categorias = promocion.getProductos().stream()
                .map(pp -> obtenerCategoria(pp.getProductoId()))
                .collect(Collectors.toList());
 
        boolean todosLaMismaCategoria = categorias.stream()
                .allMatch(c -> c != null && c.equals(categorias.get(0)));
 
        if (todosLaMismaCategoria) {
            String categoria = categorias.get(0);
            Optional<DescuentoCategoria> descuentoOpt =
                    descuentoCategoriaRepository.findByCategoria(categoria);
 
            if (descuentoOpt.isPresent()) {
                BigDecimal monto = descuentoOpt.get().getMontoDescuento();
                BigDecimal precioFinal = promocion.getPrecioPromocion().subtract(monto);
 
                // Evitar precios negativos
                if (precioFinal.compareTo(BigDecimal.ZERO) < 0) {
                    precioFinal = BigDecimal.ZERO;
                }
 
                dto.setPrecioFinal(precioFinal);
                dto.setCategoriaDescuento(categoria);
                dto.setMontoDescuentoAplicado(monto);
                return dto;
            }
        }
 
        // Sin descuento aplicable
        dto.setPrecioFinal(promocion.getPrecioPromocion());
        return dto;
    }
 
    /**
     * Consulta producto-service y devuelve la categoría del producto.
     * Si falla, retorna null (no bloquea la consulta de la promoción).
     */
    private String obtenerCategoria(Long productoId) {
        try {
            String url = "http://localhost:8087/api/productos/" + productoId;
            @SuppressWarnings("unchecked")
            Map<String, Object> data = restTemplate.getForObject(url, Map.class);
            return data != null ? (String) data.get("categoria") : null;
        } catch (RestClientException e) {
            // Si el producto no está disponible, no aplicamos descuento para ese producto
            return null;
        }
    }
 
    // ─────────────────────────────────────────────────────────────────────────
    // Métodos auxiliares existentes
    // ─────────────────────────────────────────────────────────────────────────
 
    private void validarFechas(PromocionDTO dto) {
        if (dto.getFechaFin().isBefore(dto.getFechaInicio()) || dto.getFechaFin().equals(dto.getFechaInicio())) {
            throw new RuntimeException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
    }
 
    private boolean estaDentroDeFechas(Promocion promocion) {
        LocalDate hoy = LocalDate.now();
        return !hoy.isBefore(promocion.getFechaInicio())
                && !hoy.isAfter(promocion.getFechaFin());
    }
 
    private PromocionProducto crearPromocionProducto(PromocionProductoDTO dto, Promocion promocion) {
        Map<String, Object> productoData = obtenerProducto(dto.getProductoId());
 
        PromocionProducto producto = new PromocionProducto();
        producto.setProductoId(dto.getProductoId());
        producto.setProductoNombre(productoData.get("nombre").toString());
        producto.setCantidad(dto.getCantidad());
        producto.setPromocion(promocion);
        return producto;
    }
 
    @SuppressWarnings("unchecked")
    private Map<String, Object> obtenerProducto(Long productoId) {
        try {
            String url = "http://localhost:8087/api/productos/" + productoId;
            return restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            throw new RuntimeException("El producto con ID " + productoId + " no existe en producto-service");
        }
    }
}