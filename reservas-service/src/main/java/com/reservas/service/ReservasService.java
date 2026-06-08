package com.reservas.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservas.dto.CrearReservaRequestDTO;
import com.reservas.dto.ProductoReservaDTO;
import com.reservas.dto.ReservasDTO;
import com.reservas.model.Reservas;
import com.reservas.repository.ReservasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservasService {

    private final ReservasRepository reservasRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public List<ReservasDTO> getAll() {
        return reservasRepository.findAll()
                .stream()
                .map(ReservasDTO::fromModel)
                .collect(Collectors.toList());
    }

    public ReservasDTO getById(Long id) {
        Reservas reserva = reservasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        return ReservasDTO.fromModel(reserva);
    }

    public List<ReservasDTO> getByUsuarioId(Long usuarioId) {
        return reservasRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(ReservasDTO::fromModel)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        Reservas reserva = reservasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        reservasRepository.delete(reserva);
    }

    public ReservasDTO crearReserva(CrearReservaRequestDTO request) {

        // Validar usuario
        Map<String, Object> usuario = validarLoginUsuario(
                request.getCorreo(),
                request.getPassword()
        );

        // Obtener función y película
        Map<String, Object> funcion = obtenerFuncion(request.getFuncionId());
        String peliculaTitulo = (String) funcion.get("peliculaTitulo");
        Double precioGeneral = Double.valueOf(funcion.get("precioGeneral").toString());

        // Calcular total entradas
        Double totalEntradas = precioGeneral * request.getCantidadEntradas();

        // Procesar productos
        List<ProductoReservaDTO> productosConPrecio = new ArrayList<>();
        Double totalProductos = 0.0;

        if (request.getProductos() != null && !request.getProductos().isEmpty()) {
            for (ProductoReservaDTO item : request.getProductos()) {
                Map<String, Object> producto = buscarProductoPorNombre(item.getNombre());
                Double precioUnitario = Double.valueOf(producto.get("precio").toString());
                Double subtotal = precioUnitario * item.getCantidad();

                item.setPrecioUnitario(precioUnitario);
                item.setSubtotal(subtotal);
                productosConPrecio.add(item);
                totalProductos += subtotal;
            }
        }

        Double totalGeneral = totalEntradas + totalProductos;

        // Convertir productos a JSON para guardar
        String productosJson = "[]";
        try {
            productosJson = objectMapper.writeValueAsString(productosConPrecio);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error procesando productos");
        }

        // Crear reserva
        Reservas reserva = new Reservas();
        reserva.setUsuarioId(Long.valueOf(usuario.get("id").toString()));
        reserva.setFuncionId(request.getFuncionId());
        reserva.setPeliculaTitulo(peliculaTitulo);
        reserva.setCantidadEntradas(request.getCantidadEntradas());
        reserva.setEstado("PENDIENTE");
        reserva.setProductosJson(productosJson);
        reserva.setTotalProductos(totalProductos);
        reserva.setTotalEntradas(totalEntradas);
        reserva.setTotalGeneral(totalGeneral);

        Reservas guardada = reservasRepository.save(reserva);
        return ReservasDTO.fromModel(guardada);
    }

    private Map<String, Object> validarLoginUsuario(String correo, String password) {
        try {
            String url = "http://usuario-service:8081/api/usuarios/login";
            Map<String, String> body = Map.of("correo", correo, "password", password);
            return restTemplate.postForObject(url, body, Map.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Correo o contraseña incorrectos");
        }
    }

    private Map<String, Object> obtenerFuncion(Long funcionId) {
        try {
            String url = "http://cinefunciones-service:8082/api/funciones/" + funcionId;
            return restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            throw new RuntimeException("La función con ID " + funcionId + " no existe");
        }
    }

    private Map<String, Object> buscarProductoPorNombre(String nombre) {
        try {
            String url = "http://producto-service:8087/api/productos/nombre/" + nombre;
            return restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            throw new RuntimeException("El producto '" + nombre + "' no existe");
        }
    }
}