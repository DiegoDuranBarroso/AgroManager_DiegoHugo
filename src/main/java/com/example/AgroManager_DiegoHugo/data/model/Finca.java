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

    @Column(length = 100)
    private String ciudad;

    @Column(length = 100)
    private String provincia;

    private Double area;

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

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Gerente getGerente() {
        return gerente;
    }

    public void setGerente(Gerente gerente) {
        this.gerente = gerente;
    }

    public List<Asignacion> getAsignaciones() {
        return asignaciones;
    }

    public void setAsignaciones(List<Asignacion> asignaciones) {
        this.asignaciones = asignaciones;
    }

    public List<Fichaje> getFichajes() {
        return fichajes;
    }

    public void setFichajes(List<Fichaje> fichajes) {
        this.fichajes = fichajes;
    }

    public List<Tarea> getTareas() {
        return tareas;
    }

    public void setTareas(List<Tarea> tareas) {
        this.tareas = tareas;
    }
}
