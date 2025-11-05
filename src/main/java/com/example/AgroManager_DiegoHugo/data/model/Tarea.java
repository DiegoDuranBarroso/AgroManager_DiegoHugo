package com.example.AgroManager_DiegoHugo.data.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private BigDecimal horas;

    @ManyToOne(optional = false)
    private Empleado empleado;

    @ManyToOne(optional = false)
    private Finca finca;

    public Tarea() {}

    public Tarea(LocalDate fecha, String tipo, BigDecimal horas, Empleado empleado, Finca finca) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.horas = horas;
        this.empleado = empleado;
        this.finca = finca;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getHoras() {
        return horas;
    }

    public void setHoras(BigDecimal horas) {
        this.horas = horas;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Finca getFinca() {
        return finca;
    }

    public void setFinca(Finca finca) {
        this.finca = finca;
    }
}
