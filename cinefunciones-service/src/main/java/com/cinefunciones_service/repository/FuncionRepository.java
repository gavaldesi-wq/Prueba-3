package com.cinefunciones_service.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.cinefunciones_service.model.FuncionModel;

@Repository
public interface FuncionRepository extends JpaRepository<FuncionModel, Long> {
    
    /*Buscar funciones por ID de película */
    List<FuncionModel> findByPeliculaId(Long peliculaId);

    /*Buscar funciones por ID de sala */
    List<FuncionModel> findBySalaId(Long salaId);

    /*Buscar funciones por fecha */
    List<FuncionModel> findByFecha(java.time.LocalDate fecha);

    /*Buscar funciones por estado */
    List<FuncionModel> findByEstado(String estado);

    /*Buscar funciones por formato */
    List<FuncionModel> findByFormato(String formato);

    /*Buscar funciones por idioma */
    List<FuncionModel> findByIdioma(String idioma);

    /*Buscar funciones que NO tengan el estado indicado */
    List<FuncionModel> findByEstadoNot(String estado);


}
