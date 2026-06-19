package com.comentarios.model;
 
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Entity
@Table(name = "comentarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comentario {
 
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
 
    @Column(nullable = false, length = 500)
    private String contenido;
 
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}
