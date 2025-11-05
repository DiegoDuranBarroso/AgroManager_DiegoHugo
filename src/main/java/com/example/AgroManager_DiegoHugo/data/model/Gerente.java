package com.example.AgroManager_DiegoHugo.data.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Gerente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String email;

    @Column
    private String telefono;

    @OneToOne(optional = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "gerente")
    private List<Finca> fincas;

    public Gerente() {
    }

    public Gerente(String nombre, String email, String telefono, Usuario usuario) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
