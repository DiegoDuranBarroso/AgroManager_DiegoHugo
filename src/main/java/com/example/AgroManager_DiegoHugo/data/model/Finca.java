package com.example.AgroManager_DiegoHugo.data.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Finca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFinca estado;

    @ManyToOne(optional = false)
    private Gerente gerente;

    @OneToMany(mappedBy = "finca")
    private List<Asignacion> asignaciones;

    @OneToMany(mappedBy = "finca")
    private List<Fichaje> fichajes;

    @OneToMany(mappedBy = "finca")
    private List<Tarea> tareas;

    public Finca() {
    }

    public Finca(String nombre, EstadoFinca estado, Gerente gerente) {
        this.nombre = nombre;
        this.estado = estado;
        this.gerente = gerente;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public EstadoFinca getEstado() {
        return estado;
    }

    public void setEstado(EstadoFinca estado) {
        this.estado = estado;
    }

    public Gerente getGerente() {
        return gerente;
    }

    public void setGerente(Gerente gerente) {
        this.gerente = gerente;
    }
}
