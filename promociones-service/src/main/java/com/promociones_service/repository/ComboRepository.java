package com.promociones_service.repository;

import com.promociones_service.model.Combo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComboRepository extends JpaRepository<Combo, Long> {
    List<Combo> findByActivoTrue();
    Optional<Combo> findByNombreAndActivoTrue(String nombre);

}