package com.promociones_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.promociones_service.DTO.ComboDTO;
import com.promociones_service.DTO.ProductoComboDTO;
import com.promociones_service.model.Combo;
import com.promociones_service.repository.ComboRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("ComboServiceTest - Pruebas unitarias del servicio de combos")
class ComboServiceTest {

    @Mock
    private ComboRepository comboRepository;

    @Mock
    private RestTemplate restTemplate;

    private ComboService comboService;

    @BeforeEach
    void setUp() {
        comboService = new ComboService(comboRepository, restTemplate);
    }

    // ==================== TESTS PARA getAll() ====================

    @Test
    @DisplayName("getAll - Debe retornar lista de todos los combos")
    void testGetAll_Success() {
        // Given
        Combo combo1 = new Combo();
        combo1.setId(1L);
        combo1.setNombre("Combo Clásico");
        combo1.setDescripcion("Palomitas + Bebida");
        combo1.setPrecioCombo(120.0);
        combo1.setActivo(true);

        Combo combo2 = new Combo();
        combo2.setId(2L);
        combo2.setNombre("Combo Premium");
        combo2.setDescripcion("Palomitas + Bebida + Snack");
        combo2.setPrecioCombo(180.0);
        combo2.setActivo(true);

        when(comboRepository.findAll()).thenReturn(List.of(combo1, combo2));

        // When
        List<ComboDTO> result = comboService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Combo Clásico", result.get(0).getNombre());
        assertEquals("Combo Premium", result.get(1).getNombre());
        verify(comboRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAll - Debe retornar lista vacía cuando no hay combos")
    void testGetAll_Empty() {
        // Given
        when(comboRepository.findAll()).thenReturn(List.of());

        // When
        List<ComboDTO> result = comboService.getAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(comboRepository, times(1)).findAll();
    }

    // ==================== TESTS PARA getActivos() ====================

    @Test
    @DisplayName("getActivos - Debe retornar solo combos activos")
    void testGetActivos_Success() {
        // Given
        Combo combo1 = new Combo();
        combo1.setId(1L);
        combo1.setNombre("Combo Clásico");
        combo1.setActivo(true);

        Combo combo2 = new Combo();
        combo2.setId(2L);
        combo2.setNombre("Combo Premium");
        combo2.setActivo(true);

        when(comboRepository.findByActivoTrue()).thenReturn(List.of(combo1, combo2));

        // When
        List<ComboDTO> result = comboService.getActivos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> c.getActivo() != null));
        verify(comboRepository, times(1)).findByActivoTrue();
    }

    @Test
    @DisplayName("getActivos - Debe retornar lista vacía cuando no hay combos activos")
    void testGetActivos_Empty() {
        // Given
        when(comboRepository.findByActivoTrue()).thenReturn(List.of());

        // When
        List<ComboDTO> result = comboService.getActivos();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(comboRepository, times(1)).findByActivoTrue();
    }

    // ==================== TESTS PARA getById() ====================

    @Test
    @DisplayName("getById - Debe retornar combo cuando existe")
    void testGetById_Success() {
        // Given
        Long comboId = 1L;
        Combo combo = new Combo();
        combo.setId(comboId);
        combo.setNombre("Combo Clásico");
        combo.setPrecioCombo(120.0);
        combo.setActivo(true);

        when(comboRepository.findById(comboId)).thenReturn(Optional.of(combo));

        // When
        ComboDTO result = comboService.getById(comboId);

        // Then
        assertNotNull(result);
        assertEquals(comboId, result.getId());
        assertEquals("Combo Clásico", result.getNombre());
        verify(comboRepository, times(1)).findById(comboId);
    }

    @Test
    @DisplayName("getById - Debe lanzar excepción cuando combo no existe")
    void testGetById_NotFound() {
        // Given
        Long comboId = 999L;
        when(comboRepository.findById(comboId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> comboService.getById(comboId));
        assertEquals("Combo no encontrado", exception.getMessage());
        verify(comboRepository, times(1)).findById(comboId);
    }

    // ==================== TESTS PARA getByNombre() ====================

    @Test
    @DisplayName("getByNombre - Debe retornar combo cuando existe y está activo")
    void testGetByNombre_Success() {
        // Given
        String nombre = "Combo Clásico";
        Combo combo = new Combo();
        combo.setId(1L);
        combo.setNombre(nombre);
        combo.setActivo(true);

        when(comboRepository.findByNombreAndActivoTrue(nombre)).thenReturn(Optional.of(combo));

        // When
        ComboDTO result = comboService.getByNombre(nombre);

        // Then
        assertNotNull(result);
        assertEquals(nombre, result.getNombre());
        verify(comboRepository, times(1)).findByNombreAndActivoTrue(nombre);
    }

    @Test
    @DisplayName("getByNombre - Debe lanzar excepción cuando combo no existe o no está activo")
    void testGetByNombre_NotFoundOrInactive() {
        // Given
        String nombre = "Combo Inexistente";
        when(comboRepository.findByNombreAndActivoTrue(nombre)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> comboService.getByNombre(nombre));
        assertTrue(exception.getMessage().contains("no existe"));
        verify(comboRepository, times(1)).findByNombreAndActivoTrue(nombre);
    }

    // ==================== TESTS PARA save() ====================

    @Test
    @DisplayName("save - Debe crear combo exitosamente con productos válidos")
    void testSave_Success() {
        // Given
        ProductoComboDTO producto = new ProductoComboDTO();
        producto.setNombre("Palomitas");
        producto.setCantidad(1);

        ComboDTO dto = new ComboDTO();
        dto.setNombre("Combo Clásico");
        dto.setDescripcion("Palomitas + Bebida");
        dto.setPrecioCombo(120.0);
        dto.setProductos(List.of(producto));

        // Mock: validación de producto
        when(restTemplate.getForObject(contains("/productos/nombre/"), eq(Map.class)))
                .thenReturn(Map.of("id", 1L, "nombre", "Palomitas"));

        Combo comboGuardado = new Combo();
        comboGuardado.setId(1L);
        comboGuardado.setNombre("Combo Clásico");
        comboGuardado.setActivo(true);

        when(comboRepository.save(any(Combo.class))).thenReturn(comboGuardado);

        // When
        ComboDTO result = comboService.save(dto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Combo Clásico", result.getNombre());
        verify(restTemplate, times(1)).getForObject(contains("/productos/nombre/"), eq(Map.class));
        verify(comboRepository, times(1)).save(any(Combo.class));
    }

    @Test
    @DisplayName("save - Debe lanzar excepción si producto no existe")
    void testSave_ProductoNotFound() {
        // Given
        ProductoComboDTO producto = new ProductoComboDTO();
        producto.setNombre("ProductoInexistente");

        ComboDTO dto = new ComboDTO();
        dto.setNombre("Combo Inválido");
        dto.setProductos(List.of(producto));

        // Mock: producto no existe
        when(restTemplate.getForObject(contains("/productos/nombre/"), eq(Map.class)))
                .thenThrow(new RestClientException("Not found"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> comboService.save(dto));
        assertTrue(exception.getMessage().contains("no existe"));
        verify(comboRepository, never()).save(any(Combo.class));
    }

    // ==================== TESTS PARA update() ====================

    @Test
    @DisplayName("update - Debe actualizar combo exitosamente")
    void testUpdate_Success() {
        // Given
        Long comboId = 1L;
        ProductoComboDTO producto = new ProductoComboDTO();
        producto.setNombre("Bebida");

        ComboDTO dto = new ComboDTO();
        dto.setNombre("Combo Premium");
        dto.setDescripcion("Actualizado");
        dto.setPrecioCombo(180.0);
        dto.setActivo(true);
        dto.setProductos(List.of(producto));

        Combo comboExistente = new Combo();
        comboExistente.setId(comboId);
        comboExistente.setNombre("Combo Clásico");

        when(comboRepository.findById(comboId)).thenReturn(Optional.of(comboExistente));
        when(restTemplate.getForObject(contains("/productos/nombre/"), eq(Map.class)))
                .thenReturn(Map.of("id", 2L, "nombre", "Bebida"));

        Combo comboActualizado = new Combo();
        comboActualizado.setId(comboId);
        comboActualizado.setNombre("Combo Premium");

        when(comboRepository.save(any(Combo.class))).thenReturn(comboActualizado);

        // When
        ComboDTO result = comboService.update(comboId, dto);

        // Then
        assertNotNull(result);
        assertEquals(comboId, result.getId());
        assertEquals("Combo Premium", result.getNombre());
        verify(comboRepository, times(1)).findById(comboId);
        verify(comboRepository, times(1)).save(any(Combo.class));
    }

    @Test
    @DisplayName("update - Debe lanzar excepción si combo no existe")
    void testUpdate_NotFound() {
        // Given
        Long comboId = 999L;
        ComboDTO dto = new ComboDTO();

        when(comboRepository.findById(comboId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> comboService.update(comboId, dto));
        assertEquals("Combo no encontrado", exception.getMessage());
        verify(comboRepository, never()).save(any(Combo.class));
    }

    // ==================== TESTS PARA delete() ====================

    @Test
    @DisplayName("delete - Debe eliminar combo exitosamente")
    void testDelete_Success() {
        // Given
        Long comboId = 1L;
        Combo combo = new Combo();
        combo.setId(comboId);
        combo.setNombre("Combo Clásico");

        when(comboRepository.findById(comboId)).thenReturn(Optional.of(combo));

        // When
        comboService.delete(comboId);

        // Then
        verify(comboRepository, times(1)).findById(comboId);
        verify(comboRepository, times(1)).delete(combo);
    }

    @Test
    @DisplayName("delete - Debe lanzar excepción si combo no existe")
    void testDelete_NotFound() {
        // Given
        Long comboId = 999L;
        when(comboRepository.findById(comboId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> comboService.delete(comboId));
        assertEquals("Combo no encontrado", exception.getMessage());
        verify(comboRepository, never()).delete(any(Combo.class));
    }
}
