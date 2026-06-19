package com.favoritos.repository;
 
import com.favoritos.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
 
    List<Favorito> findByUsuarioId(Long usuarioId);
 
    boolean existsByUsuarioIdAndPeliculaId(Long usuarioId, Long peliculaId);
}
