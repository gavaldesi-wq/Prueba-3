package com.comentarios.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.comentarios.dto.ComentarioDTO;
import com.comentarios.service.ComentarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ComentarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ComentarioService comentarioService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ComentarioController controller = new ComentarioController(comentarioService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAllComentarios() throws Exception {
        ComentarioDTO comentario1 = ComentarioDTO.builder()
                .id(1L)
                .usuarioId(1L)
                .usuarioNombre("Juan Pérez")
                .peliculaId(1L)
                .peliculaTitulo("Inception")
                .contenido("Excelente película")
                .fechaCreacion(LocalDateTime.now())
                .build();

        ComentarioDTO comentario2 = ComentarioDTO.builder()
                .id(2L)
                .usuarioId(2L)
                .usuarioNombre("María García")
                .peliculaId(1L)
                .peliculaTitulo("Inception")
                .contenido("Me encantó")
                .fechaCreacion(LocalDateTime.now())
                .build();

        List<ComentarioDTO> comentariosList = Arrays.asList(comentario1, comentario2);
        when(comentarioService.getAll()).thenReturn(comentariosList);

        mockMvc.perform(get("/api/comentarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].usuarioNombre").value("Juan Pérez"))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(comentarioService).getAll();
    }

    @Test
    void getComentarioById() throws Exception {
        Long comentarioId = 1L;
        ComentarioDTO comentario = ComentarioDTO.builder()
                .id(comentarioId)
                .usuarioId(1L)
                .usuarioNombre("Juan Pérez")
                .peliculaId(1L)
                .peliculaTitulo("Inception")
                .contenido("Excelente película")
                .fechaCreacion(LocalDateTime.now())
                .build();

        when(comentarioService.getById(comentarioId)).thenReturn(comentario);

        mockMvc.perform(get("/api/comentarios/{id}", comentarioId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comentarioId))
                .andExpect(jsonPath("$.usuarioNombre").value("Juan Pérez"));

        verify(comentarioService).getById(comentarioId);
    }

    @Test
    void getComentarioByIdNotFound() throws Exception {
        Long comentarioId = 999L;
        when(comentarioService.getById(comentarioId))
                .thenThrow(new RuntimeException("Comentario no encontrado"));

        mockMvc.perform(get("/api/comentarios/{id}", comentarioId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Comentario no encontrado"));
    }

    @Test
    void getComentariosByPelicula() throws Exception {
        Long peliculaId = 1L;
        ComentarioDTO comentario1 = ComentarioDTO.builder()
                .id(1L)
                .usuarioId(1L)
                .usuarioNombre("Juan Pérez")
                .peliculaId(peliculaId)
                .peliculaTitulo("Inception")
                .contenido("Excelente película")
                .fechaCreacion(LocalDateTime.now())
                .build();

        List<ComentarioDTO> comentariosList = Arrays.asList(comentario1);
        when(comentarioService.getByPelicula(peliculaId)).thenReturn(comentariosList);

        mockMvc.perform(get("/api/comentarios/pelicula/{peliculaId}", peliculaId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].peliculaId").value(peliculaId));
    }

    @Test
    void saveComentario() throws Exception {
        ComentarioDTO request = ComentarioDTO.builder()
                .correo("juan@email.com")
                .password("123456")
                .peliculaId(1L)
                .contenido("Excelente película")
                .build();

        ComentarioDTO response = ComentarioDTO.builder()
                .id(1L)
                .usuarioId(1L)
                .usuarioNombre("Juan Pérez")
                .peliculaId(1L)
                .peliculaTitulo("Inception")
                .contenido("Excelente película")
                .fechaCreacion(LocalDateTime.now())
                .build();

        when(comentarioService.save(any(ComentarioDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/comentarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.usuarioNombre").value("Juan Pérez"));
    }

    @Test
    void saveComentarioConDatosInvalidos() throws Exception {
        ComentarioDTO request = ComentarioDTO.builder()
                .correo("juan@email.com")
                .password("123456")
                .peliculaId(1L)
                .build();

        mockMvc.perform(post("/api/comentarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(comentarioService, never()).save(any(ComentarioDTO.class));
    }

    @Test
    void updateComentario() throws Exception {
        Long comentarioId = 1L;
        ComentarioDTO request = ComentarioDTO.builder()
                .correo("juan@email.com")
                .password("123456")
                .contenido("Excelente película - Actualizado")
                .build();

        ComentarioDTO response = ComentarioDTO.builder()
                .id(comentarioId)
                .usuarioId(1L)
                .usuarioNombre("Juan Pérez")
                .peliculaId(1L)
                .peliculaTitulo("Inception")
                .contenido("Excelente película - Actualizado")
                .fechaCreacion(LocalDateTime.now())
                .build();

        when(comentarioService.update(eq(comentarioId), any(ComentarioDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/comentarios/{id}", comentarioId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido").value("Excelente película - Actualizado"));
    }

    @Test
    void deleteComentario() throws Exception {
        Long comentarioId = 1L;
        doNothing().when(comentarioService).delete(comentarioId, "juan@email.com", "123456");

        mockMvc.perform(delete("/api/comentarios/{id}", comentarioId)
                .param("correo", "juan@email.com")
                .param("password", "123456")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Comentario eliminado correctamente"));

        verify(comentarioService).delete(comentarioId, "juan@email.com", "123456");
    }
    
}