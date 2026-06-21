package com.producto.controller;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.producto.DTO.ProductoDTO;
import com.producto.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoControllerTest - Pruebas de endpoints del controlador de productos")
class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ProductoController controller = new ProductoController(productoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    // ==================== GET ALL ====================

    @Test
    @DisplayName("GET /api/productos - Debe retornar lista de productos")
    void testGetAll_Success() throws Exception {
        // Given
        ProductoDTO p1 = ProductoDTO.builder().id(1L).nombre("Cabritas grandes").precio(3500.0).categoria("PALOMITAS").build();
        ProductoDTO p2 = ProductoDTO.builder().id(2L).nombre("Bebida grande").precio(2000.0).categoria("BEBIDAS").build();

        when(productoService.getAll()).thenReturn(List.of(p1, p2));

        // When & Then
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Cabritas grandes")))
                .andExpect(jsonPath("$[1].nombre", is("Bebida grande")));

        verify(productoService, times(1)).getAll();
    }

    // ==================== GET BY NOMBRE ====================

    @Test
    @DisplayName("GET /api/productos/nombre/{nombre} - Debe retornar producto encontrado")
    void testGetByNombre_Success() throws Exception {
        // Given
        String nombre = "Nachos";
        ProductoDTO producto = ProductoDTO.builder().id(1L).nombre(nombre).precio(3200.0).categoria("NACHOS").build();

        when(productoService.getByNombre(nombre)).thenReturn(producto);

        // When & Then
        mockMvc.perform(get("/api/productos/nombre/{nombre}", nombre))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Nachos")))
                .andExpect(jsonPath("$.precio", is(3200.0)));

        verify(productoService, times(1)).getByNombre(nombre);
    }

    // ==================== GET BY ID ====================

    @Test
    @DisplayName("GET /api/productos/{id} - Debe retornar producto encontrado")
    void testGetById_Success() throws Exception {
        // Given
        Long id = 1L;
        ProductoDTO producto = ProductoDTO.builder().id(id).nombre("Hot dog").precio(3000.0).categoria("OTRO").build();

        when(productoService.getById(id)).thenReturn(producto);

        // When & Then
        mockMvc.perform(get("/api/productos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Hot dog")));

        verify(productoService, times(1)).getById(id);
    }

    // ==================== POST ====================

    @Test
    @DisplayName("POST /api/productos - Debe crear producto exitosamente")
    void testSave_Success() throws Exception {
        // Given
        ProductoDTO requestDto = ProductoDTO.builder().nombre("Hot dog").precio(3000.0).categoria("OTRO").build();
        ProductoDTO savedDto = ProductoDTO.builder().id(1L).nombre("Hot dog").precio(3000.0).categoria("OTRO").build();

        when(productoService.save(any(ProductoDTO.class))).thenReturn(savedDto);

        // When & Then
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Hot dog")));

        verify(productoService, times(1)).save(any(ProductoDTO.class));
    }

    @Test
    @DisplayName("POST /api/productos - Debe retornar 400 cuando faltan datos obligatorios")
    void testSave_ValidationError() throws Exception {
        // Given
        ProductoDTO requestDto = ProductoDTO.builder().nombre("").precio(null).categoria("").build();

        // When & Then
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(productoService, never()).save(any(ProductoDTO.class));
    }

    // ==================== PUT ====================

    @Test
    @DisplayName("PUT /api/productos/{id} - Debe actualizar producto exitosamente")
    void testUpdate_Success() throws Exception {
        // Given
        Long id = 1L;
        ProductoDTO requestDto = ProductoDTO.builder().nombre("Cabritas grandes promo").precio(3000.0).categoria("PALOMITAS").build();
        ProductoDTO updatedDto = ProductoDTO.builder().id(id).nombre("Cabritas grandes promo").precio(3000.0).categoria("PALOMITAS").build();

        when(productoService.update(eq(id), any(ProductoDTO.class))).thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/productos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Cabritas grandes promo")))
                .andExpect(jsonPath("$.precio", is(3000.0)));

        verify(productoService, times(1)).update(eq(id), any(ProductoDTO.class));
    }

    // ==================== DELETE ====================

    @Test
    @DisplayName("DELETE /api/productos/{id} - Debe eliminar producto exitosamente")
    void testDelete_Success() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(productoService).delete(id);

        // When & Then
        mockMvc.perform(delete("/api/productos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", is("Producto eliminado correctamente")));

        verify(productoService, times(1)).delete(id);
    }
}