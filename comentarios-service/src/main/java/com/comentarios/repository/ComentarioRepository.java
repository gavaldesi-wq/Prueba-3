package com.comentarios.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.comentarios.model.Comentario;
 
@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
 
    List<Comentario> findByPeliculaId(Long peliculaId);
 
    List<Comentario> findByUsuarioId(Long usuarioId);
}
