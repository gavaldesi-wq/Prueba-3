package com.producto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.producto.DTO.ProductoDTO;
import com.producto.model.Producto;
import com.producto.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoServiceTest - Pruebas unitarias del servicio de productos")
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        productoService = new ProductoService(productoRepository);
    }

    // ==================== getAll() ====================

    @Test
    @DisplayName("getAll - Debe retornar lista de todos los productos")
    void testGetAll_Success() {
        // Given
        Producto p1 = new Producto(1L, "Cabritas grandes", 3500.0, "PALOMITAS");
        Producto p2 = new Producto(2L, "Bebida grande", 2000.0, "BEBIDAS");

        when(productoRepository.findAll()).thenReturn(List.of(p1, p2));

        // When
        List<ProductoDTO> result = productoService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Cabritas grandes", result.get(0).getNombre());
        assertEquals("Bebida grande", result.get(1).getNombre());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAll - Debe retornar lista vacía cuando no hay productos")
    void testGetAll_Empty() {
        // Given
        when(productoRepository.findAll()).thenReturn(List.of());

        // When
        List<ProductoDTO> result = productoService.getAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productoRepository, times(1)).findAll();
    }

    // ==================== getByNombre() ====================

    @Test
    @DisplayName("getByNombre - Debe retornar producto encontrado por nombre")
    void testGetByNombre_Success() {
        // Given
        String nombre = "Cabritas grandes";
        Producto producto = new Producto(1L, nombre, 3500.0, "PALOMITAS");

        when(productoRepository.findByNombre(nombre)).thenReturn(List.of(producto));

        // When
        ProductoDTO result = productoService.getByNombre(nombre);

        // Then
        assertNotNull(result);
        assertEquals(nombre, result.getNombre());
        assertEquals(3500.0, result.getPrecio());
        verify(productoRepository, times(1)).findByNombre(nombre);
    }

    @Test
    @DisplayName("getByNombre - Debe lanzar RuntimeException cuando no existe producto")
    void testGetByNombre_NotFound() {
        // Given
        String nombre = "Producto Inexistente";
        when(productoRepository.findByNombre(nombre)).thenReturn(List.of());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productoService.getByNombre(nombre));
        assertEquals("Producto no encontrado", exception.getMessage());
        verify(productoRepository, times(1)).findByNombre(nombre);
    }

    // ==================== getById() ====================

    @Test
    @DisplayName("getById - Debe retornar producto encontrado por id")
    void testGetById_Success() {
        // Given
        Long productoId = 1L;
        Producto producto = new Producto(productoId, "Nachos", 3200.0, "NACHOS");

        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

        // When
        ProductoDTO result = productoService.getById(productoId);

        // Then
        assertNotNull(result);
        assertEquals(productoId, result.getId());
        assertEquals("Nachos", result.getNombre());
        verify(productoRepository, times(1)).findById(productoId);
    }

    @Test
    @DisplayName("getById - Debe lanzar RuntimeException cuando no existe producto")
    void testGetById_NotFound() {
        // Given
        Long productoId = 999L;
        when(productoRepository.findById(productoId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productoService.getById(productoId));
        assertEquals("Producto no encontrado", exception.getMessage());
        verify(productoRepository, times(1)).findById(productoId);
    }

    // ==================== save() ====================

    @Test
    @DisplayName("save - Debe crear producto exitosamente")
    void testSave_Success() {
        // Given
        ProductoDTO dto = ProductoDTO.builder()
                .nombre("Hot dog")
                .precio(3000.0)
                .categoria("OTRO")
                .build();

        Producto guardado = new Producto(1L, "Hot dog", 3000.0, "OTRO");

        when(productoRepository.save(any(Producto.class))).thenReturn(guardado);

        // When
        ProductoDTO result = productoService.save(dto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Hot dog", result.getNombre());
        assertEquals(3000.0, result.getPrecio());
        assertEquals("OTRO", result.getCategoria());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("save - Debe normalizar la categoría a mayúsculas")
    void testSave_NormalizaCategoria() {
        // Given
        ProductoDTO dto = ProductoDTO.builder()
                .nombre("Nachos")
                .precio(3200.0)
                .categoria("nachos")
                .build();

        Producto guardado = new Producto(1L, "Nachos", 3200.0, "NACHOS");

        when(productoRepository.save(any(Producto.class))).thenReturn(guardado);

        // When
        ProductoDTO result = productoService.save(dto);

        // Then
        assertNotNull(result);
        assertEquals("NACHOS", result.getCategoria());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    // ==================== update() ====================

    @Test
    @DisplayName("update - Debe actualizar producto exitosamente")
    void testUpdate_Success() {
        // Given
        Long productoId = 1L;
        Producto existente = new Producto(productoId, "Cabritas grandes", 3500.0, "PALOMITAS");

        ProductoDTO dto = ProductoDTO.builder()
                .nombre("Cabritas grandes promo")
                .precio(3000.0)
                .categoria("PALOMITAS")
                .build();

        when(productoRepository.findById(productoId)).thenReturn(Optional.of(existente));

        Producto actualizado = new Producto(productoId, "Cabritas grandes promo", 3000.0, "PALOMITAS");
        when(productoRepository.save(any(Producto.class))).thenReturn(actualizado);

        // When
        ProductoDTO result = productoService.update(productoId, dto);

        // Then
        assertNotNull(result);
        assertEquals("Cabritas grandes promo", result.getNombre());
        assertEquals(3000.0, result.getPrecio());
        verify(productoRepository, times(1)).findById(productoId);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("update - Debe lanzar RuntimeException cuando no existe producto")
    void testUpdate_NotFound() {
        // Given
        Long productoId = 999L;
        ProductoDTO dto = ProductoDTO.builder()
                .nombre("No existe")
                .precio(1000.0)
                .categoria("OTRO")
                .build();

        when(productoRepository.findById(productoId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productoService.update(productoId, dto));
        assertEquals("Producto no encontrado", exception.getMessage());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    // ==================== delete() ====================

    @Test
    @DisplayName("delete - Debe eliminar producto exitosamente")
    void testDelete_Success() {
        // Given
        Long productoId = 1L;
        Producto producto = new Producto(productoId, "Nachos", 3200.0, "NACHOS");

        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

        // When
        productoService.delete(productoId);

        // Then
        verify(productoRepository, times(1)).findById(productoId);
        verify(productoRepository, times(1)).delete(producto);
    }

    @Test
    @DisplayName("delete - Debe lanzar RuntimeException cuando no existe producto")
    void testDelete_NotFound() {
        // Given
        Long productoId = 999L;
        when(productoRepository.findById(productoId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productoService.delete(productoId));
        assertEquals("Producto no encontrado", exception.getMessage());
        verify(productoRepository, never()).delete(any(Producto.class));
    }
}