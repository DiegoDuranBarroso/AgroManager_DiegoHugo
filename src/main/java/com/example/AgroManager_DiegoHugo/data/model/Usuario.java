package com.example.AgroManager_DiegoHugo.data.model;

import jakarta.persistence.*;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToOne(mappedBy = "usuario")
    private Gerente gerente;

    @OneToOne(mappedBy = "usuario")
    private Empleado empleado;

    public Usuario() {}

    public Usuario(String username, String passwordHash, Rol rol, boolean activo) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = activo;
    }

    // Getters y setters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
