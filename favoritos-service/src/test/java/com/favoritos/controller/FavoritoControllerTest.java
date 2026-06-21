package com.favoritos.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.favoritos.dto.FavoritoDTO;
import com.favoritos.service.FavoritoService;
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
@DisplayName("FavoritoControllerTest - Pruebas de endpoints del controlador de favoritos")
class FavoritoControllerTest {

    @Mock
    private FavoritoService favoritoService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private FavoritoController controller;

    @BeforeEach
    void setUp() {
        controller = new FavoritoController(favoritoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    // ==================== GET ALL ====================

    @Test
    @DisplayName("GET /api/favoritos - Debe retornar lista de favoritos")
    void testGetAll_Success() throws Exception {
        // Given
        FavoritoDTO fav1 = FavoritoDTO.builder()
                .id(1L)
                .usuarioId(100L)
                .usuarioNombre("Juan")
                .peliculaId(10L)
                .peliculaTitulo("Avatar")
                .build();

        FavoritoDTO fav2 = FavoritoDTO.builder()
                .id(2L)
                .usuarioId(100L)
                .usuarioNombre("Juan")
                .peliculaId(11L)
                .peliculaTitulo("Titanic")
                .build();

        when(favoritoService.getAll()).thenReturn(List.of(fav1, fav2));

        // When & Then
        mockMvc.perform(get("/api/favoritos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].peliculaTitulo", is("Avatar")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].peliculaTitulo", is("Titanic")));

        verify(favoritoService, times(1)).getAll();
    }

    @Test
    @DisplayName("GET /api/favoritos - Debe retornar lista vacía cuando no hay favoritos")
    void testGetAll_Empty() throws Exception {
        // Given
        when(favoritoService.getAll()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/favoritos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(favoritoService, times(1)).getAll();
    }

    // ==================== GET BY ID ====================

    @Test
    @DisplayName("GET /api/favoritos/{id} - Debe retornar favorito encontrado")
    void testGetById_Success() throws Exception {
        // Given
        Long favoritoId = 1L;
        FavoritoDTO favorito = FavoritoDTO.builder()
                .id(favoritoId)
                .usuarioId(100L)
                .usuarioNombre("Juan")
                .peliculaId(10L)
                .peliculaTitulo("Avatar")
                .build();

        when(favoritoService.getById(favoritoId)).thenReturn(favorito);

        // When & Then
        mockMvc.perform(get("/api/favoritos/{id}", favoritoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.peliculaTitulo", is("Avatar")))
                .andExpect(jsonPath("$.usuarioNombre", is("Juan")));

        verify(favoritoService, times(1)).getById(favoritoId);
    }

   @Test
   @DisplayName("PUT /api/favoritos/{id} - Debe actualizar favorito exitosamente")
   void testUpdate_Success() throws Exception {
        // Given
        Long favoritoId = 1L;
        String requestJson = """
                {
                        "correo": "juan@example.com",
                        "password": "pass123",
                        "peliculaId": 11
                }
                """;

        FavoritoDTO updatedDto = FavoritoDTO.builder()
                .id(favoritoId)
                .usuarioId(100L)
                .usuarioNombre("Juan")
                .peliculaId(11L)
                .peliculaTitulo("Titanic")
                .build();

        when(favoritoService.update(eq(favoritoId), any(FavoritoDTO.class))).thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/favoritos/{id}", favoritoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.peliculaId", is(11)))
                .andExpect(jsonPath("$.peliculaTitulo", is("Titanic")));

        verify(favoritoService, times(1)).update(eq(favoritoId), any(FavoritoDTO.class));
        }
   @Test
@DisplayName("PUT /api/favoritos/{id} - Debe retornar error si no es propietario")
void testUpdate_Unauthorized() throws Exception {
    // Given
    Long favoritoId = 1L;
    String requestJson = """
            {
                "correo": "otro@example.com",
                "password": "pass123",
                "peliculaId": 11
            }
            """;

    when(favoritoService.update(eq(favoritoId), any(FavoritoDTO.class)))
            .thenThrow(new RuntimeException("No tienes permiso para editar este favorito"));

    // When & Then
    mockMvc.perform(put("/api/favoritos/{id}", favoritoId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is("No tienes permiso para editar este favorito")));

    verify(favoritoService, times(1)).update(eq(favoritoId), any(FavoritoDTO.class));
}

        @Test
        @DisplayName("PUT /api/favoritos/{id} - Debe retornar error cuando favorito no existe")
        void testUpdate_NotFound() throws Exception {
        // Given
        Long favoritoId = 999L;
        String requestJson = """
                {
                        "correo": "juan@example.com",
                        "password": "pass123",
                        "peliculaId": 11
                }
                """;

        when(favoritoService.update(eq(favoritoId), any(FavoritoDTO.class)))
                .thenThrow(new RuntimeException("Favorito no encontrado"));

        // When & Then
        mockMvc.perform(put("/api/favoritos/{id}", favoritoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Favorito no encontrado")));

        verify(favoritoService, times(1)).update(eq(favoritoId), any(FavoritoDTO.class));
        }
    @Test
    @DisplayName("GET /api/favoritos/{id} - Debe retornar error cuando no existe favorito")
    void testGetById_NotFound() throws Exception {
        // Given
        Long favoritoId = 999L;
        when(favoritoService.getById(favoritoId))
                .thenThrow(new RuntimeException("Favorito no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/favoritos/{id}", favoritoId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Favorito no encontrado")));

        verify(favoritoService, times(1)).getById(favoritoId);
    }

    // ==================== GET BY USUARIO ====================

    @Test
    @DisplayName("GET /api/favoritos/usuario/{usuarioId} - Debe retornar favoritos del usuario")
    void testGetByUsuario_Success() throws Exception {
        // Given
        Long usuarioId = 100L;
        FavoritoDTO fav1 = FavoritoDTO.builder()
                .id(1L)
                .usuarioId(usuarioId)
                .usuarioNombre("Juan")
                .peliculaId(10L)
                .peliculaTitulo("Avatar")
                .build();

        FavoritoDTO fav2 = FavoritoDTO.builder()
                .id(2L)
                .usuarioId(usuarioId)
                .usuarioNombre("Juan")
                .peliculaId(11L)
                .peliculaTitulo("Titanic")
                .build();

        when(favoritoService.getByUsuario(usuarioId)).thenReturn(List.of(fav1, fav2));

        // When & Then
        mockMvc.perform(get("/api/favoritos/usuario/{usuarioId}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].usuarioId", is(100)))
                .andExpect(jsonPath("$[0].peliculaTitulo", is("Avatar")))
                .andExpect(jsonPath("$[1].peliculaTitulo", is("Titanic")));

        verify(favoritoService, times(1)).getByUsuario(usuarioId);
    }

    // ==================== DELETE ====================

    @Test
    @DisplayName("DELETE /api/favoritos/{id} - Debe eliminar favorito exitosamente")
    void testDelete_Success() throws Exception {
        // Given
        Long favoritoId = 1L;
        String correo = "juan@example.com";
        String password = "pass123";

        // When & Then
        mockMvc.perform(delete("/api/favoritos/{id}", favoritoId)
                .param("correo", correo)
                .param("password", password))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", is("Favorito eliminado correctamente")));

        verify(favoritoService, times(1)).delete(favoritoId, correo, password);
    }

    @Test
    @DisplayName("DELETE /api/favoritos/{id} - Debe retornar error si no es propietario")
    void testDelete_Unauthorized() throws Exception {
        // Given
        Long favoritoId = 1L;
        String correo = "otro@example.com";
        String password = "pass123";

        doThrow(new RuntimeException("No tienes permiso para eliminar este favorito"))
                .when(favoritoService).delete(favoritoId, correo, password);

        // When & Then
        mockMvc.perform(delete("/api/favoritos/{id}", favoritoId)
                .param("correo", correo)
                .param("password", password))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("No tienes permiso para eliminar este favorito")));

        verify(favoritoService, times(1)).delete(favoritoId, correo, password);
    }

    @Test
    @DisplayName("DELETE /api/favoritos/{id} - Debe retornar error cuando favorito no existe")
    void testDelete_NotFound() throws Exception {
        // Given
        Long favoritoId = 999L;
        String correo = "juan@example.com";
        String password = "pass123";

        doThrow(new RuntimeException("Favorito no encontrado"))
                .when(favoritoService).delete(favoritoId, correo, password);

        // When & Then
        mockMvc.perform(delete("/api/favoritos/{id}", favoritoId)
                .param("correo", correo)
                .param("password", password))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Favorito no encontrado")));

        verify(favoritoService, times(1)).delete(favoritoId, correo, password);
    }
}
