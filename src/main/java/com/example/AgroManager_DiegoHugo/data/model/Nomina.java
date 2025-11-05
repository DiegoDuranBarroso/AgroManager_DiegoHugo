package com.example.AgroManager_DiegoHugo.data.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Nomina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate periodoInicio;

    @Column(nullable = false)
    private LocalDate periodoFin;

    @Column(nullable = false)
    private BigDecimal totalBruto;

    @Column(nullable = false)
    private String estado;

    @ManyToOne(optional = false)
    private Empleado empleado;

    public Nomina() {
    }

    public Nomina(LocalDate periodoInicio, LocalDate periodoFin, BigDecimal totalBruto, String estado, Empleado empleado) {
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
        this.totalBruto = totalBruto;
        this.estado = estado;
        this.empleado = empleado;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(LocalDate periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public LocalDate getPeriodoFin() {
        return periodoFin;
    }

    public void setPeriodoFin(LocalDate periodoFin) {
        this.periodoFin = periodoFin;
    }

    public BigDecimal getTotalBruto() {
        return totalBruto;
    }

    public void setTotalBruto(BigDecimal totalBruto) {
        this.totalBruto = totalBruto;
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
}
