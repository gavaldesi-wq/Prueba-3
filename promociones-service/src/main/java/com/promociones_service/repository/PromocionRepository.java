package com.promociones_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.promociones_service.model.Promocion;
@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {
    // Busca promociones por estado, por ejemplo ACTIVA, INACTIVA o VENCIDA
    List<Promocion> findByEstado(String estado);
}
