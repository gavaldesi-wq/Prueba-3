package com.reservas.service;

import com.reservas.dto.CrearReservaRequestDTO;
import com.reservas.dto.ReservasDTO;
import com.reservas.model.Reservas;
import com.reservas.repository.ReservasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservasService {

    private final ReservasRepository reservasRepository;
    private final RestTemplate restTemplate;

    /* Mostrar todas las reservas guardadas */
    public List<ReservasDTO> getAll() {
        return reservasRepository.findAll()
                .stream()
                .map(ReservasDTO::fromModel)
                .collect(Collectors.toList());
    }

    /* Buscar una reserva por su ID */
    public ReservasDTO getById(Long id) {
        Reservas reserva = reservasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        return ReservasDTO.fromModel(reserva);
    }

    /* Mostrar reservas por usuario */
    public List<ReservasDTO> getByUsuarioId(Long usuarioId) {
        return reservasRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(ReservasDTO::fromModel)
                .collect(Collectors.toList());
    }

    /* Eliminar una reserva */
    public void delete(Long id) {
        Reservas reserva = reservasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        reservasRepository.delete(reserva);
    }

     /* Crear una reserva */
   public ReservasDTO crearReserva(CrearReservaRequestDTO request) {

    Map<String, Object> usuario = validarLoginUsuario(
            request.getCorreo(),
            request.getPassword()
    );

    obtenerFuncion(request.getFuncionId());

    Reservas reserva = new Reservas();

    reserva.setUsuarioId(
            Long.valueOf(usuario.get("id").toString())
    );

    reserva.setFuncionId(request.getFuncionId());
    reserva.setCantidadEntradas(request.getCantidadEntradas());
    reserva.setEstado("PENDIENTE");

    Reservas guardada = reservasRepository.save(reserva);

    return ReservasDTO.fromModel(guardada);
}
    /*
     * Este método llama al login de usuario-service.
     *
     * Recibe correo y password.
     * Los manda como JSON al endpoint /api/usuarios/login.
     * Si el login está correcto, devuelve los datos del usuario.
     /*EL STRING CORREO Y EL PASSWORD VIENE DEL JSON, LO QUE PONE EL USUARIO */
    private Map<String, Object> validarLoginUsuario(String correo, String password) {
        try {
            String url = "http://localhost:8081/api/usuarios/login";

            Map<String, String> body = Map.of(
                    "correo", correo,
                    "password", password
            );

            return restTemplate.postForObject(url, body, Map.class);

        } catch (RestClientException e) {
            throw new RuntimeException("Correo o contraseña incorrectos");
        }
    }

    /*
     * Este método obtiene la función desde cinefunciones-service.
     *
     * Ejemplo:
     * Si funcionId = 1, llama a:
     * http://localhost:8082/api/funciones/1
     *
     * La respuesta JSON se convierte en Map<String, Object>.
     */
    /*EL FUNCIONESID VIENE DEL JSON QUE PONE EL USUARIO */
    private Map<String, Object> obtenerFuncion(Long funcionId) {
        try {
            String url = "http://localhost:8082/api/funciones/" + funcionId;

            return restTemplate.getForObject(url, Map.class);

        } catch (RestClientException e) {
            throw new RuntimeException("La función con ID " + funcionId + " no existe");
        }
    }
}