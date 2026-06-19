package com.favoritos.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import com.favoritos.dto.FavoritoDTO;
import com.favoritos.model.Favorito;
import com.favoritos.repository.FavoritoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoritoService {


    private final FavoritoRepository favoritoRepository;
    private final RestTemplate restTemplate;
 
private static final String USUARIOS_URL = "http://usuario-service:8081";
private static final String PELICULAS_URL = "http://peliculas-service:8084";
 
    public List<FavoritoDTO> getAll() {
        log.info("Obteniendo todos los favoritos");
        return favoritoRepository.findAll()
                .stream()
                .map(FavoritoDTO::fromModel)
                .collect(Collectors.toList());
    }
 
    public FavoritoDTO getById(Long id) {
        log.info("Buscando favorito id={}", id);
        Favorito favorito = favoritoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Favorito no encontrado id={}", id);
                    return new RuntimeException("Favorito no encontrado");
                });
        log.info("Favorito encontrado id={}", id);
        return FavoritoDTO.fromModel(favorito);
    }
 
    public List<FavoritoDTO> getByUsuario(Long usuarioId) {
        log.info("Obteniendo favoritos de usuario id={}", usuarioId);
        return favoritoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(FavoritoDTO::fromModel)
                .collect(Collectors.toList());
    }
 
    public FavoritoDTO save(FavoritoDTO dto) {
        // 1. Autenticar usuario
        Map<String, Object> usuario = autenticarUsuario(dto.getCorreo(), dto.getPassword());
        Long usuarioId = ((Number) usuario.get("id")).longValue();
 
        // 2. Verificar que no esté ya en favoritos
        if (favoritoRepository.existsByUsuarioIdAndPeliculaId(usuarioId, dto.getPeliculaId())) {
            throw new RuntimeException("Esta película ya está en tus favoritos");
        }
 
        // 3. Obtener datos de la película
        Map<String, Object> pelicula = obtenerPelicula(dto.getPeliculaId());
 
        // 4. Usar toModel() y completar con datos externos
        Favorito favorito = dto.toModel();
        favorito.setUsuarioId(usuarioId);
        favorito.setUsuarioNombre(usuario.get("nombre").toString());
        favorito.setPeliculaTitulo(pelicula.get("titulo").toString());
 
        Favorito guardado = favoritoRepository.save(favorito);
        log.info("Favorito agregado id={} usuario={} pelicula={}", guardado.getId(), guardado.getUsuarioNombre(), guardado.getPeliculaTitulo());
        return FavoritoDTO.fromModel(guardado);
    }
 
    public FavoritoDTO update(Long id, FavoritoDTO dto) {
        log.info("Actualizando favorito id={}", id);
 
        Favorito favorito = favoritoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Favorito no encontrado id={}", id);
                    return new RuntimeException("Favorito no encontrado");
                });
 
        // Autenticar para verificar que es el dueño
        Map<String, Object> usuario = autenticarUsuario(dto.getCorreo(), dto.getPassword());
        Long usuarioId = ((Number) usuario.get("id")).longValue();
 
        if (!favorito.getUsuarioId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para editar este favorito");
        }
 
        // Actualizar película
        Map<String, Object> pelicula = obtenerPelicula(dto.getPeliculaId());
        favorito.setPeliculaId(dto.getPeliculaId());
        favorito.setPeliculaTitulo(pelicula.get("titulo").toString());
 
        Favorito actualizado = favoritoRepository.save(favorito);
        log.info("Favorito actualizado id={}", id);
        return FavoritoDTO.fromModel(actualizado);
    }
 
    public void delete(Long id, String correo, String password) {
        log.info("Eliminando favorito id={}", id);
 
        // Autenticar usuario
        Map<String, Object> usuario = autenticarUsuario(correo, password);
        Long usuarioId = ((Number) usuario.get("id")).longValue();
 
        // Buscar favorito
        Favorito favorito = favoritoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Favorito no encontrado id={}", id);
                    return new RuntimeException("Favorito no encontrado");
                });
 
        // Verificar que le pertenece
        if (!favorito.getUsuarioId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar este favorito");
        }
 
        favoritoRepository.delete(favorito);
        log.info("Favorito eliminado id={} por usuario={}", id, correo);
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
