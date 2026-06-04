package com.cinefunciones_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Table(name = "funcion")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class FuncionModel {
      
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pelicula_id")
    private Long peliculaId;

    @Column(name = "pelicula_titulo") // ← NUEVO
    private String peliculaTitulo;

    @Column(name = "sala_id")
    private Long salaId;

    @Column(name = "sala_nombre") // ← NUEVO
    private String salaNombre;

    @Column(name = "sala_tipo") // ← NUEVO
    private String salaTipo;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @Column(name = "precio_general")
    private BigDecimal precioGeneral;

    @Column(name = "precio_vip")
    private BigDecimal precioVip;

    @Column(name = "estado")
    private String estado;

    @Column(name = "idioma")
    private String idioma;

    @Column(name = "formato")
    private String formato;

}