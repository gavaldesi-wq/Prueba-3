package com.favoritos.model;
 
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Entity
@Table(
    name = "favoritos",
    uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "pelicula_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
 
    @Column(name = "usuario_nombre", nullable = false, length = 100)
    private String usuarioNombre;
 
    @Column(name = "pelicula_id", nullable = false)
    private Long peliculaId;
 
    @Column(name = "pelicula_titulo", nullable = false, length = 255)
    private String peliculaTitulo;
 
    @Column(name = "fecha_agregado", nullable = false)
    private LocalDateTime fechaAgregado;
}
