package com.example.AgroManager_DiegoHugo.data.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String dni;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToOne(optional = false, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contrato> contratos;

    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fichaje> fichajes;

    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarea> tareas;

    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Nomina> nominas;

    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asignacion> asignaciones;

    public Empleado() {}

    public Empleado(String dni, String nombre, Boolean activo, Usuario usuario) {
        this.dni = dni;
        this.nombre = nombre;
        this.activo = activo;
        this.usuario = usuario;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<Contrato> getContratos() { return contratos; }
    public void setContratos(List<Contrato> contratos) { this.contratos = contratos; }

    public List<Fichaje> getFichajes() { return fichajes; }
    public void setFichajes(List<Fichaje> fichajes) { this.fichajes = fichajes; }

    public List<Tarea> getTareas() { return tareas; }
    public void setTareas(List<Tarea> tareas) { this.tareas = tareas; }

    public List<Nomina> getNominas() { return nominas; }
    public void setNominas(List<Nomina> nominas) { this.nominas = nominas; }

    public List<Asignacion> getAsignaciones() { return asignaciones; }
    public void setAsignaciones(List<Asignacion> asignaciones) { this.asignaciones = asignaciones; }
}
