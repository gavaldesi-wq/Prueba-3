package com.favoritos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.favoritos.dto.FavoritoDTO;
import com.favoritos.model.Favorito;
import com.favoritos.repository.FavoritoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("FavoritoServiceTest - Pruebas unitarias del servicio de favoritos")
class FavoritoServiceTest {

    @Mock
    private FavoritoRepository favoritoRepository;

    @Mock
    private RestTemplate restTemplate;

    private FavoritoService favoritoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        favoritoService = new FavoritoService(favoritoRepository, restTemplate);
    }

    // ==================== TESTS PARA getAll() ====================

    @Test
    @DisplayName("getAll - Debe retornar lista de todos los favoritos")
    void testGetAll_Success() {
        // Given
        Favorito fav1 = new Favorito(1L, 100L, "Juan", 10L, "Avatar", LocalDateTime.now());
        Favorito fav2 = new Favorito(2L, 100L, "Juan", 11L, "Titanic", LocalDateTime.now());

        when(favoritoRepository.findAll()).thenReturn(List.of(fav1, fav2));

        // When
        List<FavoritoDTO> result = favoritoService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Avatar", result.get(0).getPeliculaTitulo());
        assertEquals("Titanic", result.get(1).getPeliculaTitulo());
        verify(favoritoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAll - Debe retornar lista vacía cuando no hay favoritos")
    void testGetAll_Empty() {
        // Given
        when(favoritoRepository.findAll()).thenReturn(List.of());

        // When
        List<FavoritoDTO> result = favoritoService.getAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(favoritoRepository, times(1)).findAll();
    }

    // ==================== TESTS PARA getById() ====================

    @Test
    @DisplayName("getById - Debe retornar favorito encontrado por id")
    void testGetById_Success() {
        // Given
        Long favoritoId = 1L;
        Favorito favorito = new Favorito(favoritoId, 100L, "Juan", 10L, "Avatar", LocalDateTime.now());

        when(favoritoRepository.findById(favoritoId)).thenReturn(Optional.of(favorito));

        // When
        FavoritoDTO result = favoritoService.getById(favoritoId);

        // Then
        assertNotNull(result);
        assertEquals(favoritoId, result.getId());
        assertEquals("Avatar", result.getPeliculaTitulo());
        verify(favoritoRepository, times(1)).findById(favoritoId);
    }

    @Test
    @DisplayName("getById - Debe lanzar RuntimeException cuando no existe favorito")
    void testGetById_NotFound() {
        // Given
        Long favoritoId = 999L;
        when(favoritoRepository.findById(favoritoId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> favoritoService.getById(favoritoId));
        assertEquals("Favorito no encontrado", exception.getMessage());
        verify(favoritoRepository, times(1)).findById(favoritoId);
    }

    // ==================== TESTS PARA getByUsuario() ====================

    @Test
    @DisplayName("getByUsuario - Debe retornar favoritos del usuario")
    void testGetByUsuario_Success() {
        // Given
        Long usuarioId = 100L;
        Favorito fav1 = new Favorito(1L, usuarioId, "Juan", 10L, "Avatar", LocalDateTime.now());
        Favorito fav2 = new Favorito(2L, usuarioId, "Juan", 11L, "Titanic", LocalDateTime.now());

        when(favoritoRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(fav1, fav2));

        // When
        List<FavoritoDTO> result = favoritoService.getByUsuario(usuarioId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(f -> f.getUsuarioId().equals(usuarioId)));
        verify(favoritoRepository, times(1)).findByUsuarioId(usuarioId);
    }

    @Test
    @DisplayName("getByUsuario - Debe retornar lista vacía cuando usuario no tiene favoritos")
    void testGetByUsuario_NoFavorites() {
        // Given
        Long usuarioId = 100L;
        when(favoritoRepository.findByUsuarioId(usuarioId)).thenReturn(List.of());

        // When
        List<FavoritoDTO> result = favoritoService.getByUsuario(usuarioId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(favoritoRepository, times(1)).findByUsuarioId(usuarioId);
    }

    // ==================== TESTS PARA save() ====================

    @Test
    @DisplayName("save - Debe agregar favorito exitosamente con datos válidos")
    void testSave_Success() {
        // Given
        FavoritoDTO dto = FavoritoDTO.builder()
                .correo("juan@example.com")
                .password("pass123")
                .peliculaId(10L)
                .build();

        Map<String, Object> usuarioMap = Map.of("id", 100L, "nombre", "Juan");
        Map<String, Object> peliculaMap = Map.of("titulo", "Avatar", "id", 10L);

        when(restTemplate.postForObject(contains("/login"), any(), eq(Map.class)))
                .thenReturn(usuarioMap);
        when(restTemplate.getForObject(contains("/peliculas/"), eq(Map.class)))
                .thenReturn(peliculaMap);
        when(favoritoRepository.existsByUsuarioIdAndPeliculaId(100L, 10L)).thenReturn(false);

        Favorito savedFavorito = new Favorito(1L, 100L, "Juan", 10L, "Avatar", LocalDateTime.now());

        when(favoritoRepository.save(any(Favorito.class))).thenReturn(savedFavorito);

        // When
        FavoritoDTO result = favoritoService.save(dto);

        // Then
        assertNotNull(result);
        assertEquals(100L, result.getUsuarioId());
        assertEquals("Juan", result.getUsuarioNombre());
        assertEquals("Avatar", result.getPeliculaTitulo());
        verify(favoritoRepository, times(1)).existsByUsuarioIdAndPeliculaId(100L, 10L);
        verify(favoritoRepository, times(1)).save(any(Favorito.class));
    }

    @Test
    @DisplayName("save - Debe lanzar RuntimeException si película ya está en favoritos")
    void testSave_DuplicateFavorite() {
        // Given
        FavoritoDTO dto = FavoritoDTO.builder()
                .correo("juan@example.com")
                .password("pass123")
                .peliculaId(10L)
                .build();

        Map<String, Object> usuarioMap = Map.of("id", 100L, "nombre", "Juan");

        when(restTemplate.postForObject(contains("/login"), any(), eq(Map.class)))
                .thenReturn(usuarioMap);
        when(favoritoRepository.existsByUsuarioIdAndPeliculaId(100L, 10L)).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> favoritoService.save(dto));
        assertEquals("Esta película ya está en tus favoritos", exception.getMessage());
        verify(favoritoRepository, times(1)).existsByUsuarioIdAndPeliculaId(100L, 10L);
        verify(favoritoRepository, never()).save(any(Favorito.class));
    }

    @Test
    @DisplayName("save - Debe lanzar RuntimeException cuando falla autenticación")
    void testSave_AuthenticationFailed() {
        // Given
        FavoritoDTO dto = FavoritoDTO.builder()
                .correo("invalido@example.com")
                .password("wrongpass")
                .peliculaId(10L)
                .build();

        when(restTemplate.postForObject(contains("/login"), any(), eq(Map.class)))
                .thenThrow(new RestClientException("Unauthorized"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> favoritoService.save(dto));
        assertEquals("Correo o contraseña incorrectos", exception.getMessage());
        verify(favoritoRepository, never()).save(any(Favorito.class));
    }

    // ==================== TESTS PARA update() ====================

    @Test
    @DisplayName("update - Debe actualizar favorito exitosamente si es propietario")
    void testUpdate_Success() {
        // Given
        Long favoritoId = 1L;
        FavoritoDTO dto = FavoritoDTO.builder()
                .correo("juan@example.com")
                .password("pass123")
                .peliculaId(11L)
                .build();

        Favorito existingFavorito = new Favorito(favoritoId, 100L, null, 10L, null, null);

        Map<String, Object> usuarioMap = Map.of("id", 100L, "nombre", "Juan");
        Map<String, Object> peliculaMap = Map.of("titulo", "Titanic", "id", 11L);

        when(favoritoRepository.findById(favoritoId)).thenReturn(Optional.of(existingFavorito));
        when(restTemplate.postForObject(contains("/login"), any(), eq(Map.class)))
                .thenReturn(usuarioMap);
        when(restTemplate.getForObject(contains("/peliculas/"), eq(Map.class)))
                .thenReturn(peliculaMap);

        Favorito updatedFavorito = new Favorito(favoritoId, 100L, "Juan", 11L, "Titanic", LocalDateTime.now());

        when(favoritoRepository.save(any(Favorito.class))).thenReturn(updatedFavorito);

        // When
        FavoritoDTO result = favoritoService.update(favoritoId, dto);

        // Then
        assertNotNull(result);
        assertEquals(11L, result.getPeliculaId());
        assertEquals("Titanic", result.getPeliculaTitulo());
        verify(favoritoRepository, times(1)).findById(favoritoId);
        verify(favoritoRepository, times(1)).save(any(Favorito.class));
    }

    @Test
    @DisplayName("update - Debe lanzar RuntimeException si no es propietario")
    void testUpdate_UnauthorizedUser() {
        // Given
        Long favoritoId = 1L;
        FavoritoDTO dto = FavoritoDTO.builder()
                .correo("otro@example.com")
                .password("pass123")
                .peliculaId(11L)
                .build();

        Favorito existingFavorito = new Favorito(favoritoId, 100L, null, 10L, null, null);

        Map<String, Object> usuarioMap = Map.of("id", 200L, "nombre", "Otro");

        when(favoritoRepository.findById(favoritoId)).thenReturn(Optional.of(existingFavorito));
        when(restTemplate.postForObject(contains("/login"), any(), eq(Map.class)))
                .thenReturn(usuarioMap);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> favoritoService.update(favoritoId, dto));
        assertEquals("No tienes permiso para editar este favorito", exception.getMessage());
        verify(favoritoRepository, never()).save(any(Favorito.class));
    }

    // ==================== TESTS PARA delete() ====================

    @Test
    @DisplayName("delete - Debe eliminar favorito exitosamente si es propietario")
    void testDelete_Success() {
        // Given
        Long favoritoId = 1L;
        String correo = "juan@example.com";
        String password = "pass123";

        Favorito favorito = new Favorito(favoritoId, 100L, null, null, null, null);

        Map<String, Object> usuarioMap = Map.of("id", 100L, "nombre", "Juan");

        when(favoritoRepository.findById(favoritoId)).thenReturn(Optional.of(favorito));
        when(restTemplate.postForObject(contains("/login"), any(), eq(Map.class)))
                .thenReturn(usuarioMap);

        // When
        favoritoService.delete(favoritoId, correo, password);

        // Then
        verify(favoritoRepository, times(1)).findById(favoritoId);
        verify(favoritoRepository, times(1)).delete(favorito);
    }

    @Test
    @DisplayName("delete - Debe lanzar RuntimeException si no es propietario")
    void testDelete_UnauthorizedUser() {
        // Given
        Long favoritoId = 1L;
        String correo = "otro@example.com";
        String password = "pass123";

        Favorito favorito = new Favorito(favoritoId, 100L, null, null, null, null);

        Map<String, Object> usuarioMap = Map.of("id", 200L, "nombre", "Otro");

        when(favoritoRepository.findById(favoritoId)).thenReturn(Optional.of(favorito));
        when(restTemplate.postForObject(contains("/login"), any(), eq(Map.class)))
                .thenReturn(usuarioMap);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> favoritoService.delete(favoritoId, correo, password));
        assertEquals("No tienes permiso para eliminar este favorito", exception.getMessage());
        verify(favoritoRepository, never()).delete(favorito);
    }

    @Test
    @DisplayName("delete - Debe lanzar RuntimeException cuando favorito no existe")
    void testDelete_NotFound() {
        // Given
        Long favoritoId = 999L;
        String correo = "juan@example.com";
        String password = "pass123";

        Map<String, Object> usuarioMap = Map.of("id", 100L, "nombre", "Juan");

        when(favoritoRepository.findById(favoritoId)).thenReturn(Optional.empty());
        when(restTemplate.postForObject(contains("/login"), any(), eq(Map.class)))
                .thenReturn(usuarioMap);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> favoritoService.delete(favoritoId, correo, password));
        assertEquals("Favorito no encontrado", exception.getMessage());
        verify(favoritoRepository, never()).delete(any(Favorito.class));
    }
}
