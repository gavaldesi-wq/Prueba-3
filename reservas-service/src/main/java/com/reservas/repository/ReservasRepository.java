package com.reservas.repository;
import com.reservas.model.Reservas;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ReservasRepository extends JpaRepository<Reservas, Long> {
    List<Reservas> findByUsuarioId(Long usuarioId);
    List<Reservas> findByFuncionId(Long funcionId);

}
