package com.promociones_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.promociones_service.model.DescuentoCategoria;
 
@Repository
public interface DescuentoCategoriaRepository extends JpaRepository<DescuentoCategoria, Long> {
 
    Optional<DescuentoCategoria> findByCategoria(String categoria);
 
    boolean existsByCategoria(String categoria);
}
