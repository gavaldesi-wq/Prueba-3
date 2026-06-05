package com.pagos.Repository;
import com.pagos.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PagosRepository extends JpaRepository<Pagos, Long> {
    List<Pagos> findByReservaId(Long reservaId);
}
