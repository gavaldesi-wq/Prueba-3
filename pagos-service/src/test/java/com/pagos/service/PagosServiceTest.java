package com.pagos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.pagos.Repository.PagosRepository;
import com.pagos.dto.BoletaDTO;
import com.pagos.dto.CrearPagoRequestDTO;
import com.pagos.dto.PagosDTO;
import com.pagos.dto.ProductoBoletaDTO;
import com.pagos.model.Pagos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class PagosServiceTest {

    private PagosService pagosService;

    @Mock
    private PagosRepository pagosRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        pagosService = new PagosService(pagosRepository, restTemplate);
    }

    // ====== TEST: getAll() ======
    @Test
    void getAllPagos() {
        // Given
        List<Pagos> pagosList = Arrays.asList(
                crearPagoDeTest(1L, 100L, 5000.0, "TARJETA", "PAGADO"),
                crearPagoDeTest(2L, 101L, 3000.0, "EFECTIVO", "PAGADO")
        );
        when(pagosRepository.findAll()).thenReturn(pagosList);

        // When
        List<PagosDTO> resultado = pagosService.getAll();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(100L, resultado.get(0).getReservaId());
        assertEquals(101L, resultado.get(1).getReservaId());
        verify(pagosRepository).findAll();
    }

    // ====== TEST: getById() - Caso éxito ======
    @Test
    void getById() {
        // Given
        Long pagoId = 1L;
        Pagos pago = crearPagoDeTest(pagoId, 100L, 5000.0, "TARJETA", "PAGADO");
        when(pagosRepository.findById(pagoId)).thenReturn(Optional.of(pago));

        // When
        PagosDTO resultado = pagosService.getById(pagoId);

        // Then
        assertNotNull(resultado);
        assertEquals(100L, resultado.getReservaId());
        assertEquals(5000.0, resultado.getMonto());
        assertEquals("TARJETA", resultado.getMetodoPago());
        verify(pagosRepository).findById(pagoId);
    }


    // ====== TEST: getById() - Caso error: Pago no encontrado ======
    @Test
    void getByIdERROR() {
        // Given
        Long pagoId = 999L;
        when(pagosRepository.findById(pagoId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            pagosService.getById(pagoId);
        });
        assertEquals("Pago no encontrado", excepcion.getMessage());
        verify(pagosRepository).findById(pagoId);
    }

    // ====== TEST: getByReservaId() ======
    @Test
    void getByReservaId() {
        // Given
        Long reservaId = 100L;
        List<Pagos> pagosList = Arrays.asList(
                crearPagoDeTest(1L, reservaId, 5000.0, "TARJETA", "PAGADO"),
                crearPagoDeTest(2L, reservaId, 3000.0, "EFECTIVO", "PAGADO")
        );
        when(pagosRepository.findByReservaId(reservaId)).thenReturn(pagosList);

        // When
        List<PagosDTO> resultado = pagosService.getByReservaId(reservaId);

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(p -> p.getReservaId().equals(reservaId)));
        verify(pagosRepository).findByReservaId(reservaId);
    }

    // ====== TEST: pagarReserva() - Caso éxito ======
    @Test
    void pagarReserva() {
        // Given
        Long reservaId = 100L;
        CrearPagoRequestDTO request = new CrearPagoRequestDTO();
        request.setReservaId(reservaId);
        request.setMetodoPago("TARJETA");

        // Construir Map de respuesta de reservas-service
        Map<String, Object> reservaResponse = construirMapReserva(
                "Avengers Endgame",
                101L,
                3,
                30000.0,
                5000.0,
                35000.0
        );

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(reservaResponse);

        Pagos pagoGuardado = crearPagoDeTest(1L, reservaId, 41650.0, "TARJETA", "PAGADO");
        when(pagosRepository.save(any(Pagos.class))).thenReturn(pagoGuardado);

        // When
        BoletaDTO resultado = pagosService.pagarReserva(request);

        // Then
        assertNotNull(resultado);
        assertEquals(reservaId, resultado.getReservaId());
        assertEquals("Avengers Endgame", resultado.getPeliculaTitulo());
        assertEquals(3, resultado.getCantidadEntradas());
        assertEquals(30000.0, resultado.getTotalEntradas(), 0.01);
        assertEquals(5000.0, resultado.getTotalProductos(), 0.01);
        assertEquals(35000.0, resultado.getTotalGeneral(), 0.01);
        // Verificar cálculo de IVA: 35000 * 0.19 = 6650
        assertEquals(6650.0, resultado.getIva(), 0.01);
        // Verificar totalConIva: 35000 + 6650 = 41650
        assertEquals(41650.0, resultado.getTotalConIva(), 0.01);
        assertEquals("PAGADO", resultado.getEstado());
        verify(restTemplate).getForObject(anyString(), eq(Map.class));
        verify(pagosRepository).save(any(Pagos.class));
    }

    // ====== TEST: pagarReserva() - Caso error: Reserva no existe ======
    @Test
    void pagarReserva_ReservaNoExiste() {
        // Given
        Long reservaId = 999L;
        CrearPagoRequestDTO request = new CrearPagoRequestDTO();
        request.setReservaId(reservaId);
        request.setMetodoPago("TARJETA");

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenThrow(new RestClientException("Not found"));

        // When & Then
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            pagosService.pagarReserva(request);
        });
        assertTrue(excepcion.getMessage().contains("La reserva con ID " + reservaId + " no existe"));
        verify(restTemplate).getForObject(anyString(), eq(Map.class));
        verify(pagosRepository, never()).save(any(Pagos.class));
    }

    // ====== TEST: update() ======
    @Test
    void testUpdate() {
        // Given
        Long pagoId = 1L;
        Pagos pagoExistente = crearPagoDeTest(pagoId, 100L, 5000.0, "TARJETA", "PAGADO");

        PagosDTO dtoActualizado = new PagosDTO();
        dtoActualizado.setReservaId(100L);
        dtoActualizado.setMonto(6000.0);
        dtoActualizado.setMetodoPago("EFECTIVO");
        dtoActualizado.setEstado("PAGADO");

        Pagos pagoActualizado = crearPagoDeTest(pagoId, 100L, 6000.0, "EFECTIVO", "PAGADO");

        when(pagosRepository.findById(pagoId)).thenReturn(Optional.of(pagoExistente));
        when(pagosRepository.save(any(Pagos.class))).thenReturn(pagoActualizado);

        // When
        PagosDTO resultado = pagosService.update(pagoId, dtoActualizado);

        // Then
        assertNotNull(resultado);
        assertEquals(6000.0, resultado.getMonto());
        assertEquals("EFECTIVO", resultado.getMetodoPago());
        verify(pagosRepository).findById(pagoId);
        verify(pagosRepository).save(any(Pagos.class));
    }

    // ====== TEST: delete() - Caso éxito ======
    @Test
    void testDelete() {
        // Given
        Long pagoId = 1L;
        Pagos pagoExistente = crearPagoDeTest(pagoId, 100L, 5000.0, "TARJETA", "PAGADO");
        when(pagosRepository.findById(pagoId)).thenReturn(Optional.of(pagoExistente));

        // When
        pagosService.delete(pagoId);

        // Then
        verify(pagosRepository).findById(pagoId);
        verify(pagosRepository).delete(pagoExistente);
    }

    // ====== TEST: delete() - Caso error: Pago no encontrado ======
    @Test
    void testDelete_pagoNoEncontrado() {
        // Given
        Long pagoId = 999L;
        when(pagosRepository.findById(pagoId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            pagosService.delete(pagoId);
        });
        assertEquals("Pago no encontrado", excepcion.getMessage());
        verify(pagosRepository).findById(pagoId);
        verify(pagosRepository, never()).delete(any(Pagos.class));
    }

    @Test
    void testCalculoIVA() {
        // Given - Caso 1: totalGeneral = 1000 → iva = 190, totalConIva = 1190
        Long reservaId1 = 100L;
        CrearPagoRequestDTO request1 = new CrearPagoRequestDTO();
        request1.setReservaId(reservaId1);
        request1.setMetodoPago("TARJETA");

        Map<String, Object> reservaResponse1 = construirMapReserva(
                "Película 1",
                101L,
                2,
                500.0,
                500.0,
                1000.0  // totalGeneral = 1000
        );

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(reservaResponse1);

        Pagos pagoGuardado1 = crearPagoDeTest(1L, reservaId1, 1190.0, "TARJETA", "PAGADO");
        when(pagosRepository.save(any(Pagos.class))).thenReturn(pagoGuardado1);

        // When - Caso 1
        BoletaDTO resultado1 = pagosService.pagarReserva(request1);

        // Then - Caso 1
        assertEquals(190.0, resultado1.getIva(), 0.01);
        assertEquals(1190.0, resultado1.getTotalConIva(), 0.01);

        // Given - Caso 2: totalGeneral = 5000 → iva = 950, totalConIva = 5950
        reset(restTemplate, pagosRepository);

        Long reservaId2 = 101L;
        CrearPagoRequestDTO request2 = new CrearPagoRequestDTO();
        request2.setReservaId(reservaId2);
        request2.setMetodoPago("EFECTIVO");

        Map<String, Object> reservaResponse2 = construirMapReserva(
                "Película 2",
                102L,
                5,
                2500.0,
                2500.0,
                5000.0  // totalGeneral = 5000
        );

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(reservaResponse2);

        Pagos pagoGuardado2 = crearPagoDeTest(2L, reservaId2, 5950.0, "EFECTIVO", "PAGADO");
        when(pagosRepository.save(any(Pagos.class))).thenReturn(pagoGuardado2);

        // When - Caso 2
        BoletaDTO resultado2 = pagosService.pagarReserva(request2);

        // Then - Caso 2
        assertEquals(950.0, resultado2.getIva(), 0.01);
        assertEquals(5950.0, resultado2.getTotalConIva(), 0.01);

        // Given - Caso 3: totalGeneral = 0 → iva = 0, totalConIva = 0
        reset(restTemplate, pagosRepository);

        Long reservaId3 = 102L;
        CrearPagoRequestDTO request3 = new CrearPagoRequestDTO();
        request3.setReservaId(reservaId3);
        request3.setMetodoPago("TARJETA");

        Map<String, Object> reservaResponse3 = construirMapReserva(
                "Película 3",
                103L,
                0,
                0.0,
                0.0,
                0.0  // totalGeneral = 0
        );

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(reservaResponse3);

        Pagos pagoGuardado3 = crearPagoDeTest(3L, reservaId3, 0.0, "TARJETA", "PAGADO");
        when(pagosRepository.save(any(Pagos.class))).thenReturn(pagoGuardado3);

        // When - Caso 3
        BoletaDTO resultado3 = pagosService.pagarReserva(request3);

        // Then - Caso 3
        assertEquals(0.0, resultado3.getIva(), 0.01);
        assertEquals(0.0, resultado3.getTotalConIva(), 0.01);
    }



    /**
      Crea una entidad Pagos para tests
     */
    private Pagos crearPagoDeTest(Long id, Long reservaId, Double monto, String metodoPago, String estado) {
        Pagos pago = new Pagos();
        pago.setId(id);
        pago.setReservaId(reservaId);
        pago.setMonto(monto);
        pago.setMetodoPago(metodoPago);
        pago.setEstado(estado);
        return pago;
    }


     /*Construye un Map que simula la respuesta de reservas-service */

    private Map<String, Object> construirMapReserva(
            String peliculaTitulo,
            Long funcionId,
            Integer cantidadEntradas,
            Double totalEntradas,
            Double totalProductos,
            Double totalGeneral) {

        Map<String, Object> reserva = new HashMap<>();
        reserva.put("peliculaTitulo", peliculaTitulo);
        reserva.put("funcionId", funcionId);
        reserva.put("cantidadEntradas", cantidadEntradas);
        reserva.put("totalEntradas", totalEntradas);
        reserva.put("totalProductos", totalProductos);
        reserva.put("totalGeneral", totalGeneral);

        // Agregar lista de productos simulada
        List<Map<String, Object>> productos = new ArrayList<>();
        Map<String, Object> producto = new HashMap<>();
        producto.put("nombre", "Palomitas");
        producto.put("cantidad", 2);
        producto.put("precioUnitario", 2500.0);
        producto.put("subtotal", 5000.0);
        productos.add(producto);

        reserva.put("productos", productos);
        return reserva;
    }
}