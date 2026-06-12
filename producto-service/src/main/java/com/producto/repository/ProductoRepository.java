package com.producto.repository;
import com.producto.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombre(String nombre);
}
