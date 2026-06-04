package com.sala.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sala.dto.SalaDTO;
import com.sala.model.Sala;
import com.sala.repository.SalaRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SalaService {

    private final SalaRepository salaRepository;

    /*Este método devuelve una lista de todos los objetos SalaDTO,
     esto sirve para el Get del controller*/
    public List<SalaDTO> getAll() {
        return salaRepository.findAll()
                .stream()
                .map(SalaDTO::fromModel)
                .collect(Collectors.toList());
    }

    /*Este metodo devuelve un objeto SalaDTO por su id,
     esto sirve para el Get del controller*/
    public SalaDTO getById(Long id) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));
        return SalaDTO.fromModel(sala);
    }

    /*Este método crea una nueva sala, sirve para el POST del controller*/
    public SalaDTO save(SalaDTO dto) {
        Sala sala = dto.toModel();
        Sala guardada = salaRepository.save(sala);
        return SalaDTO.fromModel(guardada);
    }

    /*Este método actualiza una sala existente, sirve para el PUT del controller*/
    public SalaDTO update(Long id, SalaDTO dto){
        Sala s = salaRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Sala no encontrada"));
        s.setNombre(dto.getNombre());
        s.setCapacidad(dto.getCapacidad());
        s.setTipoSala(dto.getTipoSala());
        Sala actualizada = salaRepository.save(s);
        return SalaDTO.fromModel(actualizada);
    }

    /*Este método elimina una sala por su id, sirve para el DELETE del controller*/
    public void delete(Long id) {
        salaRepository.deleteById(id);
    }

}
