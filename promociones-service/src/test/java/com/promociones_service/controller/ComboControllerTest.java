package com.promociones_service.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.ArgumentMatchers;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.promociones_service.DTO.ComboDTO;
import com.promociones_service.assemblers.ComboModelAssembler;
import com.promociones_service.service.ComboService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@WebMvcTest(ComboControllerV2.class)
@DisplayName("ComboControllerTest - Pruebas de endpoints del controlador de combos")
class ComboControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComboService comboService;

    @MockBean
    private ComboModelAssembler assembler;

    // ==================== GET ALL COMBOS ====================

    @Test
    @DisplayName("GET /combos/v2 - Debe retornar lista de todos los combos")
    void testListarCombos_Success() throws Exception {
        ComboDTO combo1 = ComboDTO.builder()
                .id(1L).nombre("Combo Clásico").descripcion("Palomitas + Bebida")
                .precioCombo(120.0).activo(true).build();

        ComboDTO combo2 = ComboDTO.builder()
                .id(2L).nombre("Combo Premium").descripcion("Palomitas + Bebida + Snack")
                .precioCombo(180.0).activo(true).build();

        when(comboService.getAll()).thenReturn(List.of(combo1, combo2));
        when(assembler.toModel(ArgumentMatchers.any(ComboDTO.class)))
                .thenAnswer(invocation -> {
                    ComboDTO dto = invocation.getArgument(0);
                    return org.springframework.hateoas.EntityModel.of(dto);
                });

        mockMvc.perform(get("/combos/v2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.comboDTOList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.comboDTOList[0].nombre", is("Combo Clásico")))
                .andExpect(jsonPath("$._embedded.comboDTOList[1].nombre", is("Combo Premium")));

        verify(comboService, times(1)).getAll();
        verify(assembler, times(2)).toModel(ArgumentMatchers.any(ComboDTO.class));
    }

    @Test
    @DisplayName("GET /combos/v2 - Debe retornar lista vacía cuando no hay combos")
    void testListarCombos_Empty() throws Exception {
        when(comboService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/combos/v2"))
                .andExpect(status().isOk());

        verify(comboService, times(1)).getAll();
    }

    // ==================== GET ACTIVE COMBOS ====================

    @Test
    @DisplayName("GET /combos/v2/activos - Debe retornar lista de combos activos")
    void testListarCombosActivos_Success() throws Exception {
        ComboDTO combo1 = ComboDTO.builder()
                .id(1L).nombre("Combo Clásico").descripcion("Palomitas + Bebida")
                .precioCombo(120.0).activo(true).build();

        ComboDTO combo2 = ComboDTO.builder()
                .id(2L).nombre("Combo Premium").descripcion("Palomitas + Bebida + Snack")
                .precioCombo(180.0).activo(true).build();

        when(comboService.getActivos()).thenReturn(List.of(combo1, combo2));
        when(assembler.toModel(ArgumentMatchers.any(ComboDTO.class)))
                .thenAnswer(invocation -> {
                    ComboDTO dto = invocation.getArgument(0);
                    return org.springframework.hateoas.EntityModel.of(dto);
                });

        mockMvc.perform(get("/combos/v2/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.comboDTOList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.comboDTOList[0].activo", is(true)))
                .andExpect(jsonPath("$._embedded.comboDTOList[1].activo", is(true)));

        verify(comboService, times(1)).getActivos();
        verify(assembler, times(2)).toModel(ArgumentMatchers.any(ComboDTO.class));
    }

    @Test
    @DisplayName("GET /combos/v2/activos - Debe retornar lista vacía cuando no hay combos activos")
    void testListarCombosActivos_Empty() throws Exception {
        when(comboService.getActivos()).thenReturn(List.of());

        mockMvc.perform(get("/combos/v2/activos"))
                .andExpect(status().isOk());

        verify(comboService, times(1)).getActivos();
    }

    // ==================== GET BY ID ====================

    @Test
    @DisplayName("GET /combos/v2/{id} - Debe retornar combo encontrado")
    void testObtenerCombo_Success() throws Exception {
        Long comboId = 1L;
        ComboDTO combo = ComboDTO.builder()
                .id(comboId).nombre("Combo Clásico").descripcion("Palomitas + Bebida")
                .precioCombo(120.0).activo(true).build();

        when(comboService.getById(comboId)).thenReturn(combo);
        when(assembler.toModel(combo)).thenReturn(org.springframework.hateoas.EntityModel.of(combo));

        mockMvc.perform(get("/combos/v2/{id}", comboId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Combo Clásico")))
                .andExpect(jsonPath("$.precioCombo", is(120.0)));

        verify(comboService, times(1)).getById(comboId);
        verify(assembler, times(1)).toModel(combo);
    }

    @Test
    @DisplayName("GET /combos/v2/{id} - Debe retornar error cuando combo no existe")
    void testObtenerCombo_NotFound() throws Exception {
        Long comboId = 999L;
        when(comboService.getById(comboId))
                .thenThrow(new RuntimeException("Combo no encontrado"));

        mockMvc.perform(get("/combos/v2/{id}", comboId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", is("Combo no encontrado")));

        verify(comboService, times(1)).getById(comboId);
    }

    // ==================== GET BY NAME ====================

    @Test
    @DisplayName("GET /combos/v2/nombre/{nombre} - Debe retornar combo por nombre")
    void testObtenerComboPorNombre_Success() throws Exception {
        String nombre = "Combo Clásico";
        ComboDTO combo = ComboDTO.builder()
                .id(1L).nombre(nombre).descripcion("Palomitas + Bebida")
                .precioCombo(120.0).activo(true).build();

        when(comboService.getByNombre(nombre)).thenReturn(combo);
        when(assembler.toModel(combo)).thenReturn(org.springframework.hateoas.EntityModel.of(combo));

        mockMvc.perform(get("/combos/v2/nombre/{nombre}", nombre))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Combo Clásico")))
                .andExpect(jsonPath("$.activo", is(true)));

        verify(comboService, times(1)).getByNombre(nombre);
        verify(assembler, times(1)).toModel(combo);
    }

    @Test
    @DisplayName("GET /combos/v2/nombre/{nombre} - Debe retornar error cuando combo no existe")
    void testObtenerComboPorNombre_NotFound() throws Exception {
        String nombre = "ComboInexistente";
        when(comboService.getByNombre(nombre))
                .thenThrow(new RuntimeException("El combo '" + nombre + "' no existe o no está activo"));

        mockMvc.perform(get("/combos/v2/nombre/{nombre}", nombre))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", is("El combo '" + nombre + "' no existe o no está activo")));

        verify(comboService, times(1)).getByNombre(nombre);
    }
}