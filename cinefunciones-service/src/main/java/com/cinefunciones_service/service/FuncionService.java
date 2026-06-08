package com.cinefunciones_service.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import com.cinefunciones_service.dto.FuncionDTO;
import com.cinefunciones_service.model.FuncionModel;
import com.cinefunciones_service.repository.FuncionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FuncionService {

    private final FuncionRepository funcionRepository;
    private final RestTemplate restTemplate;

    /* Este método devuelve una lista de todos los objetos FuncionDTO */
    public List<FuncionDTO> getAll() {
        return funcionRepository.findAll()
                .stream()
                .map(FuncionDTO::fromModel)
                .collect(Collectors.toList());
    }

    /* Este metodo devuelve un objeto FuncionDTO por su id */
    public FuncionDTO getById(Long id) {
        FuncionModel funcion = funcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Función no encontrada"));
        return FuncionDTO.fromModel(funcion);
    }

    /* Este método devuelve funciones filtradas por película */
    public List<FuncionDTO> getByPelicula(Long peliculaId) {
        return funcionRepository.findByPeliculaId(peliculaId)
                .stream()
                .map(FuncionDTO::fromModel)
                .collect(Collectors.toList());
    }

    /* Este método devuelve funciones filtradas por sala */
    public List<FuncionDTO> getBySala(Long salaId) {
        return funcionRepository.findBySalaId(salaId)
                .stream()
                .map(FuncionDTO::fromModel)
                .collect(Collectors.toList());
    }

    /* Este método devuelve funciones filtradas por fecha */
    public List<FuncionDTO> getByFecha(String fecha) {
        return funcionRepository.findByFecha(java.time.LocalDate.parse(fecha))
                .stream()
                .map(FuncionDTO::fromModel)
                .collect(Collectors.toList());
    }

    /* Este método devuelve funciones filtradas por estado */
    public List<FuncionDTO> getByEstado(String estado) {
        return funcionRepository.findByEstado(estado.toUpperCase())
                .stream()
                .map(FuncionDTO::fromModel)
                .collect(Collectors.toList());
    }

    /* Este método devuelve solo funciones disponibles */
    public List<FuncionDTO> getDisponibles() {
        return funcionRepository.findByEstadoNot("CANCELADA")
                .stream()
                .map(FuncionDTO::fromModel)
                .collect(Collectors.toList());
    }

    /* Este método devuelve funciones filtradas por formato */
    public List<FuncionDTO> getByFormato(String formato) {
        return funcionRepository.findByFormato(formato.toUpperCase())
                .stream()
                .map(FuncionDTO::fromModel)
                .collect(Collectors.toList());
    }

    /* Este método devuelve funciones filtradas por idioma */
    public List<FuncionDTO> getByIdioma(String idioma) {
        return funcionRepository.findByIdioma(idioma.toUpperCase())
                .stream()
                .map(FuncionDTO::fromModel)
                .collect(Collectors.toList());
    }

    /* Este método crea una nueva función, obteniendo nombres de película y sala */
    public FuncionDTO save(FuncionDTO dto) {
        // Validar horarios
        validarHorariosValidos(dto);
        
        // Obtener y validar película
        Map<String, Object> peliculaData = obtenerPelicula(dto.getPeliculaId());
        dto.setPeliculaTitulo((String) peliculaData.get("titulo"));
        
        // Obtener y validar sala
        Map<String, Object> salaData = obtenerSala(dto.getSalaId());
        dto.setSalaNombre((String) salaData.get("nombre"));
        dto.setSalaTipo((String) salaData.get("tipoSala"));
        
        FuncionModel funcion = dto.toModel();
        FuncionModel guardada = funcionRepository.save(funcion);
        return FuncionDTO.fromModel(guardada);
    }

    /* Este método actualiza una función existente */
    public FuncionDTO update(Long id, FuncionDTO dto) {
        FuncionModel f = funcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Función no encontrada"));
        
        // Validar horarios
        validarHorariosValidos(dto);
        
        // Obtener y validar película
        Map<String, Object> peliculaData = obtenerPelicula(dto.getPeliculaId());
        
        // Obtener y validar sala
        Map<String, Object> salaData = obtenerSala(dto.getSalaId());
        
        // Actualizar todos los campos
        f.setPeliculaId(dto.getPeliculaId());
        f.setPeliculaTitulo((String) peliculaData.get("titulo"));
        f.setSalaId(dto.getSalaId());
        f.setSalaNombre((String) salaData.get("nombre"));
        f.setSalaTipo(((String) salaData.get("tipoSala")).toUpperCase());
        f.setFecha(dto.getFecha());
        f.setHoraInicio(dto.getHoraInicio());
        f.setHoraFin(dto.getHoraFin());
        f.setPrecioGeneral(dto.getPrecioGeneral());
        f.setPrecioVip(dto.getPrecioVip());
        f.setEstado(dto.getEstado().toUpperCase());
        f.setIdioma(dto.getIdioma().toUpperCase());
        f.setFormato(dto.getFormato().toUpperCase());
        
        FuncionModel actualizada = funcionRepository.save(f);
        return FuncionDTO.fromModel(actualizada);
    }

    /* Este método elimina una función por su id */
    public void delete(Long id) {
        funcionRepository.deleteById(id);
    }

    /* Método privado para validar que los horarios son válidos */
    private void validarHorariosValidos(FuncionDTO dto) {
        if (dto.getHoraFin().isBefore(dto.getHoraInicio()) || dto.getHoraFin().equals(dto.getHoraInicio())) {
            throw new RuntimeException("La hora de fin debe ser posterior a la hora de inicio");
        }
    }
    
    /* Método privado para obtener datos de la película desde el microservicio */
    @SuppressWarnings("unchecked")
    private Map<String, Object> obtenerPelicula(Long peliculaId) {
        try {
            String url = "http://peliculas-service:8084/api/peliculas/" + peliculaId;
            return restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            throw new RuntimeException("La película con ID " + peliculaId + " no existe en el servicio de películas");
        }
    }
    
    /* Método privado para obtener datos de la sala desde el microservicio */
    @SuppressWarnings("unchecked")
    private Map<String, Object> obtenerSala(Long salaId) {
        try {
            String url = "http://salas-service:8083/api/salas/" + salaId;
            return restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            throw new RuntimeException("La sala con ID " + salaId + " no existe en el servicio de salas");
        }
    }

}
