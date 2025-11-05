package com.example.AgroManager_DiegoHugo.data.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    @Column(nullable = false)
    private Boolean activa = true;

    @ManyToOne(optional = false)
    private Empleado empleado;

    @ManyToOne(optional = false)
    private Finca finca;

    public Asignacion() {
    }

    public Asignacion(LocalDate fechaInicio, LocalDate fechaFin, Boolean activa, Empleado empleado, Finca finca) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activa = activa;
        this.empleado = empleado;
        this.finca = finca;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
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
