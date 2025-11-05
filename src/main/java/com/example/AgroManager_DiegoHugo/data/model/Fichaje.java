package com.example.AgroManager_DiegoHugo.data.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Fichaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant inicio;

    private Instant fin;

    @Column(nullable = false)
    private String estado;

    @ManyToOne(optional = false)
    private Empleado empleado;

    @ManyToOne(optional = false)
    private Finca finca;

    public Fichaje() {}

    public Fichaje(Instant inicio, Instant fin, String estado, Empleado empleado, Finca finca) {
        this.inicio = inicio;
        this.fin = fin;
        this.estado = estado;
        this.empleado = empleado;
        this.finca = finca;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getInicio() {
        return inicio;
    }

    public void setInicio(Instant inicio) {
        this.inicio = inicio;
    }

    public Instant getFin() {
        return fin;
    }

    public void setFin(Instant fin) {
        this.fin = fin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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
