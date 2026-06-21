package com.comentarios.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.comentarios.dto.ComentarioDTO;
import com.comentarios.model.Comentario;
import com.comentarios.repository.ComentarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ComentarioServiceTest {

    private ComentarioService comentarioService;

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        comentarioService = new ComentarioService(comentarioRepository, restTemplate);
    }

    // ====== TEST: getAll() ======
    @Test
    void getAllComentarios() {
        // Given
        List<Comentario> comentariosList = Arrays.asList(
                crearComentario(1L, 1L, "Juan Pérez", 1L, "Inception", "Excelente película", LocalDateTime.now()),
                crearComentario(2L, 2L, "María García", 1L, "Inception", "Me encantó", LocalDateTime.now())
        );
        when(comentarioRepository.findAll()).thenReturn(comentariosList);

        // When
        List<ComentarioDTO> resultado = comentarioService.getAll();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getUsuarioNombre());
        assertEquals("María García", resultado.get(1).getUsuarioNombre());
        verify(comentarioRepository).findAll();
    }

    // ====== TEST: getById() - Caso éxito ======
    @Test
    void getComentarioById() {
        // Given
        Long comentarioId = 1L;
        Comentario comentario = crearComentario(comentarioId, 1L, "Juan Pérez", 1L, "Inception", "Excelente película", LocalDateTime.now());
        when(comentarioRepository.findById(comentarioId)).thenReturn(Optional.of(comentario));

        // When
        ComentarioDTO resultado = comentarioService.getById(comentarioId);

        // Then
        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.getUsuarioNombre());
        assertEquals("Excelente película", resultado.getContenido());
        verify(comentarioRepository).findById(comentarioId);
    }

    // ====== TEST: getById() - Caso error ======
    @Test
    void getComentarioByIdERROR() {
        // Given
        Long comentarioId = 999L;
        when(comentarioRepository.findById(comentarioId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            comentarioService.getById(comentarioId);
        });
        assertEquals("Comentario no encontrado", excepcion.getMessage());
        verify(comentarioRepository).findById(comentarioId);
    }

    // ====== TEST: getByPelicula() ======
    @Test
    void getComentariosByPelicula() {
        // Given
        Long peliculaId = 1L;
        List<Comentario> comentariosList = Arrays.asList(
                crearComentario(1L, 1L, "Juan Pérez", peliculaId, "Inception", "Excelente película", LocalDateTime.now()),
                crearComentario(2L, 2L, "María García", peliculaId, "Inception", "Me encantó", LocalDateTime.now())
        );
        when(comentarioRepository.findByPeliculaId(peliculaId)).thenReturn(comentariosList);

        // When
        List<ComentarioDTO> resultado = comentarioService.getByPelicula(peliculaId);

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(c -> c.getPeliculaId().equals(peliculaId)));
        verify(comentarioRepository).findByPeliculaId(peliculaId);
    }

    // ====== TEST: getByUsuario() ======
    @Test
    void getComentariosByUsuario() {
        // Given
        Long usuarioId = 1L;
        List<Comentario> comentariosList = Arrays.asList(
                crearComentario(1L, usuarioId, "Juan Pérez", 1L, "Inception", "Excelente película", LocalDateTime.now()),
                crearComentario(2L, usuarioId, "Juan Pérez", 2L, "The Matrix", "Clásico", LocalDateTime.now())
        );
        when(comentarioRepository.findByUsuarioId(usuarioId)).thenReturn(comentariosList);

        // When
        List<ComentarioDTO> resultado = comentarioService.getByUsuario(usuarioId);

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(c -> c.getUsuarioId().equals(usuarioId)));
        verify(comentarioRepository).findByUsuarioId(usuarioId);
    }

    // ====== TEST: save() - Caso éxito ======
    @Test
    void saveComentario() {
        // Given
        ComentarioDTO request = ComentarioDTO.builder()
                .correo("juan@email.com")
                .password("123456")
                .peliculaId(1L)
                .contenido("Excelente película")
                .build();

        // Mock respuesta de autenticación
        Map<String, Object> usuarioResponse = new HashMap<>();
        usuarioResponse.put("id", 1);
        usuarioResponse.put("nombre", "Juan Pérez");
        usuarioResponse.put("correo", "juan@email.com");

        // Mock respuesta de película
        Map<String, Object> peliculaResponse = new HashMap<>();
        peliculaResponse.put("id", 1);
        peliculaResponse.put("titulo", "Inception");
        peliculaResponse.put("clasificacion", "PG-13");

        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenReturn(usuarioResponse);
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(peliculaResponse);

        Comentario comentarioGuardado = crearComentario(1L, 1L, "Juan Pérez", 1L, "Inception", "Excelente película", LocalDateTime.now());
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentarioGuardado);

        // When
        ComentarioDTO resultado = comentarioService.save(request);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan Pérez", resultado.getUsuarioNombre());
        assertEquals("Inception", resultado.getPeliculaTitulo());
        assertEquals("Excelente película", resultado.getContenido());
        verify(restTemplate).postForObject(anyString(), any(), eq(Map.class));
        verify(restTemplate).getForObject(anyString(), eq(Map.class));
        verify(comentarioRepository).save(any(Comentario.class));
    }

    // ====== TEST: save() - Caso error: Autenticación fallida ======
    @Test
    void saveComentario_AutenticacionFallida() {
        // Given
        ComentarioDTO request = ComentarioDTO.builder()
                .correo("juan@email.com")
                .password("incorrecto")
                .peliculaId(1L)
                .contenido("Excelente película")
                .build();

        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenThrow(new RestClientException("Authentication failed"));

        // When & Then
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            comentarioService.save(request);
        });
        assertEquals("Correo o contraseña incorrectos", excepcion.getMessage());
        verify(restTemplate).postForObject(anyString(), any(), eq(Map.class));
        verify(comentarioRepository, never()).save(any(Comentario.class));
    }

    // ====== TEST: save() - Caso error: Película no encontrada ======
    @Test
    void saveComentario_PeliculaNoEncontrada() {
        // Given
        ComentarioDTO request = ComentarioDTO.builder()
                .correo("juan@email.com")
                .password("123456")
                .peliculaId(999L)
                .contenido("Excelente película")
                .build();

        Map<String, Object> usuarioResponse = new HashMap<>();
        usuarioResponse.put("id", 1);
        usuarioResponse.put("nombre", "Juan Pérez");

        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenReturn(usuarioResponse);
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenThrow(new RestClientException("Película no encontrada"));

        // When & Then
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            comentarioService.save(request);
        });
        assertTrue(excepcion.getMessage().contains("La película con ID 999 no existe"));
        verify(restTemplate).postForObject(anyString(), any(), eq(Map.class));
        verify(restTemplate).getForObject(anyString(), eq(Map.class));
        verify(comentarioRepository, never()).save(any(Comentario.class));
    }

    // ====== TEST: update() - Caso éxito ======
    @Test
    void updateComentario() {
        // Given
        Long comentarioId = 1L;
        ComentarioDTO request = ComentarioDTO.builder()
                .correo("juan@email.com")
                .password("123456")
                .contenido("Excelente película - Actualizado")
                .build();

        Comentario comentarioExistente = crearComentario(comentarioId, 1L, "Juan Pérez", 1L, "Inception", "Excelente película", LocalDateTime.now());

        Map<String, Object> usuarioResponse = new HashMap<>();
        usuarioResponse.put("id", 1);
        usuarioResponse.put("nombre", "Juan Pérez");

        when(comentarioRepository.findById(comentarioId)).thenReturn(Optional.of(comentarioExistente));
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenReturn(usuarioResponse);

        Comentario comentarioActualizado = crearComentario(comentarioId, 1L, "Juan Pérez", 1L, "Inception", "Excelente película - Actualizado", LocalDateTime.now());
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentarioActualizado);

        // When
        ComentarioDTO resultado = comentarioService.update(comentarioId, request);

        // Then
        assertNotNull(resultado);
        assertEquals("Excelente película - Actualizado", resultado.getContenido());
        verify(comentarioRepository).findById(comentarioId);
        verify(restTemplate).postForObject(anyString(), any(), eq(Map.class));
        verify(comentarioRepository).save(any(Comentario.class));
    }

    // ====== TEST: update() - Caso error: Sin permiso ======
    @Test
    void updateComentario_SinPermiso() {
        // Given
        Long comentarioId = 1L;
        ComentarioDTO request = ComentarioDTO.builder()
                .correo("otro@email.com")
                .password("123456")
                .contenido("Intento de edición")
                .build();

        Comentario comentarioExistente = crearComentario(comentarioId, 1L, "Juan Pérez", 1L, "Inception", "Excelente película", LocalDateTime.now());

        Map<String, Object> usuarioResponse = new HashMap<>();
        usuarioResponse.put("id", 2);
        usuarioResponse.put("nombre", "Otro Usuario");

        when(comentarioRepository.findById(comentarioId)).thenReturn(Optional.of(comentarioExistente));
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenReturn(usuarioResponse);

        // When & Then
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            comentarioService.update(comentarioId, request);
        });
        assertEquals("No tienes permiso para editar este comentario", excepcion.getMessage());
        verify(comentarioRepository).findById(comentarioId);
        verify(restTemplate).postForObject(anyString(), any(), eq(Map.class));
        verify(comentarioRepository, never()).save(any(Comentario.class));
    }

    // ====== TEST: delete() - Caso éxito ======
    @Test
    void deleteComentario() {
        // Given
        Long comentarioId = 1L;
        String correo = "juan@email.com";
        String password = "123456";

        Comentario comentarioExistente = crearComentario(comentarioId, 1L, "Juan Pérez", 1L, "Inception", "Excelente película", LocalDateTime.now());

        Map<String, Object> usuarioResponse = new HashMap<>();
        usuarioResponse.put("id", 1);
        usuarioResponse.put("nombre", "Juan Pérez");

        when(comentarioRepository.findById(comentarioId)).thenReturn(Optional.of(comentarioExistente));
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenReturn(usuarioResponse);

        // When
        comentarioService.delete(comentarioId, correo, password);

        // Then
        verify(comentarioRepository).findById(comentarioId);
        verify(restTemplate).postForObject(anyString(), any(), eq(Map.class));
        verify(comentarioRepository).delete(comentarioExistente);
    }

    // ====== TEST: delete() - Caso error: Sin permiso ======
    @Test
    void deleteComentario_SinPermiso() {
        // Given
        Long comentarioId = 1L;
        String correo = "otro@email.com";
        String password = "123456";

        Comentario comentarioExistente = crearComentario(comentarioId, 1L, "Juan Pérez", 1L, "Inception", "Excelente película", LocalDateTime.now());

        Map<String, Object> usuarioResponse = new HashMap<>();
        usuarioResponse.put("id", 2);
        usuarioResponse.put("nombre", "Otro Usuario");

        when(comentarioRepository.findById(comentarioId)).thenReturn(Optional.of(comentarioExistente));
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenReturn(usuarioResponse);

        // When & Then
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            comentarioService.delete(comentarioId, correo, password);
        });
        assertEquals("No tienes permiso para eliminar este comentario", excepcion.getMessage());
        verify(comentarioRepository).findById(comentarioId);
        verify(restTemplate).postForObject(anyString(), any(), eq(Map.class));
        verify(comentarioRepository, never()).delete(any(Comentario.class));
    }

    // ====== MÉTODOS HELPER ======

    private Comentario crearComentario(Long id, Long usuarioId, String usuarioNombre,
                                      Long peliculaId, String peliculaTitulo,
                                      String contenido, LocalDateTime fechaCreacion) {
        Comentario comentario = new Comentario();
        comentario.setId(id);
        comentario.setUsuarioId(usuarioId);
        comentario.setUsuarioNombre(usuarioNombre);
        comentario.setPeliculaId(peliculaId);
        comentario.setPeliculaTitulo(peliculaTitulo);
        comentario.setContenido(contenido);
        comentario.setFechaCreacion(fechaCreacion != null ? fechaCreacion : LocalDateTime.now());
        return comentario;
    }
}