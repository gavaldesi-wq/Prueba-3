package com.peliculas.repository;

import com.peliculas.model.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    List<Pelicula> findByGeneroId(Long generoId);
    List<Pelicula> findByClasificacion(String clasificacion);
}