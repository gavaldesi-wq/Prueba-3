package com.promociones_service.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promociones_service.DTO.ComboDTO;
import com.promociones_service.DTO.ProductoComboDTO;
import com.promociones_service.service.ComboService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@WebMvcTest(ComboController.class)
@DisplayName("ComboControllerTest - Pruebas del controller original con endpoints CRUD")
class ComboControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComboService comboService;

    @Autowired
    private ObjectMapper objectMapper;

    // ======================================================================
    // ==================== GET - LISTAR TODOS ==============================
    // ======================================================================

    @Test
    @DisplayName("GET /api/combos - Debe retornar lista de todos los combos")
    void testGetAll_Success() throws Exception {
        // Given
        ComboDTO combo1 = ComboDTO.builder()
                .id(1L).nombre("Combo Clásico").descripcion("Palomitas + Bebida")
                .precioCombo(120.0).activo(true).build();

        ComboDTO combo2 = ComboDTO.builder()
                .id(2L).nombre("Combo Premium").descripcion("Palomitas + Bebida + Snack")
                .precioCombo(180.0).activo(true).build();

        when(comboService.getAll()).thenReturn(List.of(combo1, combo2));

        // When & Then
        mockMvc.perform(get("/api/combos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Combo Clásico")))
                .andExpect(jsonPath("$[1].nombre", is("Combo Premium")));

        verify(comboService, times(1)).getAll();
    }

    @Test
    @DisplayName("GET /api/combos - Debe retornar lista vacía cuando no hay combos")
    void testGetAll_Empty() throws Exception {
        // Given
        when(comboService.getAll()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/combos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(comboService, times(1)).getAll();
    }

    // ======================================================================
    // ==================== GET - LISTAR ACTIVOS ============================
    // ======================================================================

    @Test
    @DisplayName("GET /api/combos/activos - Debe retornar lista de combos activos")
    void testGetActivos_Success() throws Exception {
        // Given
        ComboDTO combo1 = ComboDTO.builder()
                .id(1L).nombre("Combo Clásico").descripcion("Palomitas + Bebida")
                .precioCombo(120.0).activo(true).build();

        ComboDTO combo2 = ComboDTO.builder()
                .id(2L).nombre("Combo Premium").descripcion("Palomitas + Bebida + Snack")
                .precioCombo(180.0).activo(true).build();

        when(comboService.getActivos()).thenReturn(List.of(combo1, combo2));

        // When & Then
        mockMvc.perform(get("/api/combos/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].activo", is(true)))
                .andExpect(jsonPath("$[1].activo", is(true)));

        verify(comboService, times(1)).getActivos();
    }

    @Test
    @DisplayName("GET /api/combos/activos - Debe retornar lista vacía cuando no hay combos activos")
    void testGetActivos_Empty() throws Exception {
        // Given
        when(comboService.getActivos()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/combos/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(comboService, times(1)).getActivos();
    }

    // ======================================================================
    // ==================== GET - BUSCAR POR ID =============================
    // ======================================================================

    @Test
    @DisplayName("GET /api/combos/{id} - Debe retornar combo encontrado")
    void testGetById_Success() throws Exception {
        // Given
        Long comboId = 1L;
        ComboDTO combo = ComboDTO.builder()
                .id(comboId).nombre("Combo Clásico").descripcion("Palomitas + Bebida")
                .precioCombo(120.0).activo(true).build();

        when(comboService.getById(comboId)).thenReturn(combo);

        // When & Then
        mockMvc.perform(get("/api/combos/{id}", comboId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Combo Clásico")))
                .andExpect(jsonPath("$.precioCombo", is(120.0)));

        verify(comboService, times(1)).getById(comboId);
    }

    @Test
    @DisplayName("GET /api/combos/{id} - Debe retornar error cuando combo no existe")
    void testGetById_NotFound() throws Exception {
        // Given
        Long comboId = 999L;
        when(comboService.getById(comboId))
                .thenThrow(new RuntimeException("Combo no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/combos/{id}", comboId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", is("Combo no encontrado")));

        verify(comboService, times(1)).getById(comboId);
    }

    // ======================================================================
    // ==================== GET - BUSCAR POR NOMBRE =========================
    // ======================================================================

    @Test
    @DisplayName("GET /api/combos/nombre/{nombre} - Debe retornar combo por nombre")
    void testGetByNombre_Success() throws Exception {
        // Given
        String nombre = "Combo Clásico";
        ComboDTO combo = ComboDTO.builder()
                .id(1L).nombre(nombre).descripcion("Palomitas + Bebida")
                .precioCombo(120.0).activo(true).build();

        when(comboService.getByNombre(nombre)).thenReturn(combo);

        // When & Then
        mockMvc.perform(get("/api/combos/nombre/{nombre}", nombre))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Combo Clásico")))
                .andExpect(jsonPath("$.activo", is(true)));

        verify(comboService, times(1)).getByNombre(nombre);
    }

    @Test
    @DisplayName("GET /api/combos/nombre/{nombre} - Debe retornar error cuando combo no existe")
    void testGetByNombre_NotFound() throws Exception {
        // Given
        String nombre = "ComboInexistente";
        when(comboService.getByNombre(nombre))
                .thenThrow(new RuntimeException("El combo '" + nombre + "' no existe o no está activo"));

        // When & Then
        mockMvc.perform(get("/api/combos/nombre/{nombre}", nombre))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", is("El combo '" + nombre + "' no existe o no está activo")));

        verify(comboService, times(1)).getByNombre(nombre);
    }

    // ======================================================================
    // ==================== POST - CREAR COMBO ==============================
    // ======================================================================

    @Test
    @DisplayName("POST /api/combos - Debe crear combo exitosamente")
    void testSave_Success() throws Exception {
        // Given
        ComboDTO dto = crearComboDTO();
        ComboDTO comboGuardado = crearComboGuardado(dto);

        when(comboService.save(any(ComboDTO.class))).thenReturn(comboGuardado);

        // When & Then
        mockMvc.perform(post("/api/combos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Combo Familiar")))
                .andExpect(jsonPath("$.descripcion", is("2 Hamburguesas + 2 Papas")))
                .andExpect(jsonPath("$.precioCombo", is(25.99)))
                .andExpect(jsonPath("$.productos", hasSize(2)))
                .andExpect(jsonPath("$.productos[0].nombre", is("Hamburguesa")))
                .andExpect(jsonPath("$.productos[0].cantidad", is(2)))
                .andExpect(jsonPath("$.productos[1].nombre", is("Papa Grande")))
                .andExpect(jsonPath("$.productos[1].cantidad", is(2)));

        verify(comboService, times(1)).save(any(ComboDTO.class));
    }

    


    @Test
    @DisplayName("PUT /api/combos/{id} - Debe actualizar combo exitosamente")
    void testUpdate_Success() throws Exception {
        // Given
        Long comboId = 1L;
        ComboDTO dto = crearComboDTO();
        ComboDTO comboActualizado = crearComboGuardado(dto);

        when(comboService.update(eq(comboId), any(ComboDTO.class))).thenReturn(comboActualizado);

        // When & Then
        mockMvc.perform(put("/api/combos/{id}", comboId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Combo Familiar")))
                .andExpect(jsonPath("$.descripcion", is("2 Hamburguesas + 2 Papas")))
                .andExpect(jsonPath("$.precioCombo", is(25.99)))
                .andExpect(jsonPath("$.productos", hasSize(2)));

        verify(comboService, times(1)).update(eq(comboId), any(ComboDTO.class));
    }

    @Test
    @DisplayName("PUT /api/combos/{id} - Debe retornar error cuando combo no existe")
    void testUpdate_NotFound() throws Exception {
        // Given
        Long comboId = 999L;
        ComboDTO dto = crearComboDTO();

        when(comboService.update(eq(comboId), any(ComboDTO.class)))
                .thenThrow(new RuntimeException("Combo no encontrado"));

        // When & Then
        mockMvc.perform(put("/api/combos/{id}", comboId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", is("Combo no encontrado")));

        verify(comboService, times(1)).update(eq(comboId), any(ComboDTO.class));
    }



    // ======================================================================
    // ==================== DELETE - ELIMINAR COMBO =========================
    // ======================================================================

    @Test
    @DisplayName("DELETE /api/combos/{id} - Debe eliminar combo exitosamente")
    void testDelete_Success() throws Exception {
        // Given
        Long comboId = 1L;
        doNothing().when(comboService).delete(comboId);

        // When & Then
        mockMvc.perform(delete("/api/combos/{id}", comboId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", is("Combo eliminado")));

        verify(comboService, times(1)).delete(comboId);
    }

    @Test
    @DisplayName("DELETE /api/combos/{id} - Debe retornar error cuando combo no existe")
    void testDelete_NotFound() throws Exception {
        // Given
        Long comboId = 999L;
        doThrow(new RuntimeException("Combo no encontrado"))
                .when(comboService).delete(comboId);

        // When & Then
        mockMvc.perform(delete("/api/combos/{id}", comboId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", is("Combo no encontrado")));

        verify(comboService, times(1)).delete(comboId);
    }

    // ======================================================================
    // ==================== MÉTODOS AUXILIARES ==============================
    // ======================================================================

    private ComboDTO crearComboDTO() {
        ProductoComboDTO producto1 = new ProductoComboDTO();
        producto1.setNombre("Hamburguesa");
        producto1.setCantidad(2);

        ProductoComboDTO producto2 = new ProductoComboDTO();
        producto2.setNombre("Papa Grande");
        producto2.setCantidad(2);

        return ComboDTO.builder()
                .nombre("Combo Familiar")
                .descripcion("2 Hamburguesas + 2 Papas")
                .precioCombo(25.99)
                .productos(List.of(producto1, producto2))
                .activo(true)
                .build();
    }

    private ComboDTO crearComboGuardado(ComboDTO dto) {
        return ComboDTO.builder()
                .id(1L)
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precioCombo(dto.getPrecioCombo())
                .productos(dto.getProductos())
                .activo(dto.getActivo())
                .build();
    }
}