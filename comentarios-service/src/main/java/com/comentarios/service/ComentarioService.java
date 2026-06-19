package com.comentarios.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.comentarios.dto.ComentarioDTO;
import com.comentarios.model.Comentario;
import com.comentarios.repository.ComentarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
@Slf4j
@Service
@RequiredArgsConstructor
public class ComentarioService {
 
    private final ComentarioRepository comentarioRepository;
    private final RestTemplate restTemplate;
 
private static final String USUARIOS_URL = "http://usuario-service:8081";
private static final String PELICULAS_URL = "http://peliculas-service:8084";
    public List<ComentarioDTO> getAll() {
        log.info("Obteniendo todos los comentarios");
        return comentarioRepository.findAll()
                .stream()
                .map(ComentarioDTO::fromModel)
                .collect(Collectors.toList());
    }
 
    public ComentarioDTO getById(Long id) {
        log.info("Buscando comentario id={}", id);
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Comentario no encontrado id={}", id);
                    return new RuntimeException("Comentario no encontrado");
                });
        log.info("Comentario encontrado id={}", id);
        return ComentarioDTO.fromModel(comentario);
    }
 
    public List<ComentarioDTO> getByPelicula(Long peliculaId) {
        log.info("Obteniendo comentarios de pelicula id={}", peliculaId);
        return comentarioRepository.findByPeliculaId(peliculaId)
                .stream()
                .map(ComentarioDTO::fromModel)
                .collect(Collectors.toList());
    }
 
    public List<ComentarioDTO> getByUsuario(Long usuarioId) {
        log.info("Obteniendo comentarios de usuario id={}", usuarioId);
        return comentarioRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(ComentarioDTO::fromModel)
                .collect(Collectors.toList());
    }
 
    public ComentarioDTO save(ComentarioDTO dto) {
        // 1. Autenticar usuario
        Map<String, Object> usuario = autenticarUsuario(dto.getCorreo(), dto.getPassword());
 
        // 2. Obtener datos de la película
        Map<String, Object> pelicula = obtenerPelicula(dto.getPeliculaId());
 
        // 3. Usar toModel() y completar con datos externos
        Comentario comentario = dto.toModel();
        comentario.setUsuarioId(((Number) usuario.get("id")).longValue());
        comentario.setUsuarioNombre(usuario.get("nombre").toString());
        comentario.setPeliculaTitulo(pelicula.get("titulo").toString());
 
        Comentario guardado = comentarioRepository.save(comentario);
        log.info("Comentario creado id={} por usuario={}", guardado.getId(), guardado.getUsuarioNombre());
        return ComentarioDTO.fromModel(guardado);
    }
 
    public ComentarioDTO update(Long id, ComentarioDTO dto) {
        log.info("Actualizando comentario id={}", id);
 
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Comentario no encontrado id={}", id);
                    return new RuntimeException("Comentario no encontrado");
                });
 
        // Autenticar para verificar que es el dueño
        Map<String, Object> usuario = autenticarUsuario(dto.getCorreo(), dto.getPassword());
        Long usuarioId = ((Number) usuario.get("id")).longValue();
 
        if (!comentario.getUsuarioId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para editar este comentario");
        }
 
        comentario.setContenido(dto.getContenido());
 
        Comentario actualizado = comentarioRepository.save(comentario);
        log.info("Comentario actualizado id={}", id);
        return ComentarioDTO.fromModel(actualizado);
    }
 
    public void delete(Long id, String correo, String password) {
        log.info("Eliminando comentario id={}", id);
 
        // Autenticar usuario
        Map<String, Object> usuario = autenticarUsuario(correo, password);
        Long usuarioId = ((Number) usuario.get("id")).longValue();
 
        // Buscar comentario
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Comentario no encontrado id={}", id);
                    return new RuntimeException("Comentario no encontrado");
                });
 
        // Verificar que le pertenece
        if (!comentario.getUsuarioId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar este comentario");
        }
 
        comentarioRepository.delete(comentario);
        log.info("Comentario eliminado id={} por usuario={}", id, correo);
    }
 
    @SuppressWarnings("unchecked")
    private Map<String, Object> autenticarUsuario(String correo, String password) {
        try {
            String url = USUARIOS_URL + "/api/usuarios/login";
            Map<String, String> loginRequest = Map.of("correo", correo, "password", password);
            Map<String, Object> usuario = restTemplate.postForObject(url, loginRequest, Map.class);
            if (usuario == null) throw new RuntimeException("No se pudo autenticar el usuario");
            log.info("Usuario autenticado correo={}", correo);
            return usuario;
        } catch (RestClientException e) {
            log.warn("Autenticación fallida correo={}", correo);
            throw new RuntimeException("Correo o contraseña incorrectos");
        }
    }
 
    @SuppressWarnings("unchecked")
    private Map<String, Object> obtenerPelicula(Long peliculaId) {
        try {
            String url = PELICULAS_URL + "/api/peliculas/" + peliculaId;
            Map<String, Object> pelicula = restTemplate.getForObject(url, Map.class);
            if (pelicula == null) throw new RuntimeException("Película no encontrada");
            return pelicula;
        } catch (RestClientException e) {
            throw new RuntimeException("La película con ID " + peliculaId + " no existe en peliculas-service");
        }
    }
}