package com.pagos.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagos.dto.BoletaDTO;
import com.pagos.dto.CrearPagoRequestDTO;
import com.pagos.dto.PagosDTO;
import com.pagos.dto.ProductoBoletaDTO;
import com.pagos.service.PagosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PagosControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PagosService pagosService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        PagosController controller = new PagosController(pagosService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ====== TEST: GET /api/pagos ======
    @Test
    void getAllPagos() throws Exception {
        // Given
        PagosDTO pago1 = crearPagoDTO(1L, 100L, 5000.0, "TARJETA", "PAGADO");
        PagosDTO pago2 = crearPagoDTO(2L, 101L, 3000.0, "EFECTIVO", "PAGADO");
        List<PagosDTO> pagosList = Arrays.asList(pago1, pago2);
        when(pagosService.getAll()).thenReturn(pagosList);

        // When & Then
        mockMvc.perform(get("/api/pagos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].reservaId").value(100L))
                .andExpect(jsonPath("$[0].monto").value(5000.0))
                .andExpect(jsonPath("$[0].metodoPago").value("TARJETA"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].reservaId").value(101L));

        verify(pagosService).getAll();
    }

    // ====== TEST: GET /api/pagos/{id} - Caso éxito ======
    @Test
    void getPagoById() throws Exception {
        // Given
        Long pagoId = 1L;
        PagosDTO pago = crearPagoDTO(pagoId, 100L, 5000.0, "TARJETA", "PAGADO");
        when(pagosService.getById(pagoId)).thenReturn(pago);

        // When & Then
        mockMvc.perform(get("/api/pagos/{id}", pagoId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pagoId))
                .andExpect(jsonPath("$.reservaId").value(100L))
                .andExpect(jsonPath("$.monto").value(5000.0))
                .andExpect(jsonPath("$.metodoPago").value("TARJETA"))
                .andExpect(jsonPath("$.estado").value("PAGADO"));

        verify(pagosService).getById(pagoId);
    }

    // ====== TEST: GET /api/pagos/reserva/{reservaId} ======
    @Test
    void getPagosByReservaId() throws Exception {
        // Given
        Long reservaId = 100L;
        PagosDTO pago1 = crearPagoDTO(1L, reservaId, 5000.0, "TARJETA", "PAGADO");
        PagosDTO pago2 = crearPagoDTO(2L, reservaId, 3000.0, "EFECTIVO", "PAGADO");
        List<PagosDTO> pagosList = Arrays.asList(pago1, pago2);
        when(pagosService.getByReservaId(reservaId)).thenReturn(pagosList);

        // When & Then
        mockMvc.perform(get("/api/pagos/reserva/{reservaId}", reservaId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].reservaId").value(reservaId))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].reservaId").value(reservaId));

        verify(pagosService).getByReservaId(reservaId);
    }

    // ====== TEST: POST /api/pagos/pagar - Caso éxito ======
    @Test
    void pagarReserva() throws Exception {
        // Given
        CrearPagoRequestDTO request = new CrearPagoRequestDTO();
        request.setReservaId(100L);
        request.setMetodoPago("TARJETA");

        BoletaDTO boleta = crearBoletaDTO(
                1L,
                100L,
                "Avengers Endgame",
                101L,
                3,
                30000.0,
                5000.0,
                35000.0,
                6650.0,
                41650.0
        );

        when(pagosService.pagarReserva(any(CrearPagoRequestDTO.class)))
                .thenReturn(boleta);

        // When & Then
        mockMvc.perform(post("/api/pagos/pagar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagoId").value(1L))
                .andExpect(jsonPath("$.reservaId").value(100L))
                .andExpect(jsonPath("$.peliculaTitulo").value("Avengers Endgame"))
                .andExpect(jsonPath("$.funcionId").value(101L))
                .andExpect(jsonPath("$.cantidadEntradas").value(3))
                .andExpect(jsonPath("$.totalEntradas").value(30000.0))
                .andExpect(jsonPath("$.totalProductos").value(5000.0))
                .andExpect(jsonPath("$.totalGeneral").value(35000.0))
                .andExpect(jsonPath("$.iva").value(6650.0))
                .andExpect(jsonPath("$.totalConIva").value(41650.0))
                .andExpect(jsonPath("$.metodoPago").value("TARJETA"))
                .andExpect(jsonPath("$.estado").value("PAGADO"));

        verify(pagosService).pagarReserva(any(CrearPagoRequestDTO.class));
    }

    // ====== TEST: POST /api/pagos/pagar - Caso error: Validación fallida ======
    @Test
    void pagarReservaConDatosInvalidos() throws Exception {
        // Given - Request inválido (sin reservaId)
        String requestJson = "{\"metodoPago\": \"TARJETA\"}";

        // When & Then
        mockMvc.perform(post("/api/pagos/pagar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(pagosService, never()).pagarReserva(any(CrearPagoRequestDTO.class));
    }

    // ====== TEST: PUT /api/pagos/{id} ======
    @Test
    void updatePago() throws Exception {
        // Given
        Long pagoId = 1L;
        PagosDTO dtoActualizado = crearPagoDTO(pagoId, 100L, 6000.0, "EFECTIVO", "PAGADO");

        when(pagosService.update(eq(pagoId), any(PagosDTO.class)))
                .thenReturn(dtoActualizado);

        // When & Then
        mockMvc.perform(put("/api/pagos/{id}", pagoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pagoId))
                .andExpect(jsonPath("$.monto").value(6000.0))
                .andExpect(jsonPath("$.metodoPago").value("EFECTIVO"));

        verify(pagosService).update(eq(pagoId), any(PagosDTO.class));
    }

    // ====== TEST: DELETE /api/pagos/{id} ======
    @Test
    void deletePago() throws Exception {
        // Given
        Long pagoId = 1L;
        doNothing().when(pagosService).delete(pagoId);

        // When & Then
        mockMvc.perform(delete("/api/pagos/{id}", pagoId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Pago eliminado"));

        verify(pagosService, times(1)).delete(pagoId);
    }

    // ====== MÉTODOS HELPER ======

    /**
     * Crea un DTO Pagos para tests
     */
    private PagosDTO crearPagoDTO(Long id, Long reservaId, Double monto, String metodoPago, String estado) {
        PagosDTO pago = new PagosDTO();
        pago.setId(id);
        pago.setReservaId(reservaId);
        pago.setMonto(monto);
        pago.setMetodoPago(metodoPago);
        pago.setEstado(estado);
        return pago;
    }

    /**
     * Crea un DTO Boleta para tests
     */
    private BoletaDTO crearBoletaDTO(
            Long pagoId,
            Long reservaId,
            String peliculaTitulo,
            Long funcionId,
            Integer cantidadEntradas,
            Double totalEntradas,
            Double totalProductos,
            Double totalGeneral,
            Double iva,
            Double totalConIva) {

        List<ProductoBoletaDTO> productos = new ArrayList<>();
        ProductoBoletaDTO producto = new ProductoBoletaDTO();
        producto.setNombre("Palomitas");
        producto.setCantidad(2);
        producto.setPrecioUnitario(2500.0);
        producto.setSubtotal(5000.0);
        productos.add(producto);

        return BoletaDTO.builder()
                .pagoId(pagoId)
                .reservaId(reservaId)
                .peliculaTitulo(peliculaTitulo)
                .funcionId(funcionId)
                .cantidadEntradas(cantidadEntradas)
                .totalEntradas(totalEntradas)
                .totalProductos(totalProductos)
                .totalGeneral(totalGeneral)
                .subtotal(totalGeneral)
                .iva(iva)
                .totalConIva(totalConIva)
                .productos(productos)
                .metodoPago("TARJETA")
                .estado("PAGADO")
                .build();
    }
}