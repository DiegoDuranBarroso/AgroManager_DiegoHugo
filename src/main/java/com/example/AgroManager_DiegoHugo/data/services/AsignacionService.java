package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.Asignacion;
import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Finca;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsignacionService {

    List<Asignacion> encontrarTodas();

    Optional<Asignacion> encontrarPorId(Long id);

    List<Asignacion> encontrarPorEmpleado(Long empleadoId);

    List<Asignacion> encontrarPorFinca(Long fincaId);

    List<Asignacion> encontrarActivas();

    Asignacion asignarEmpleadoAFinca(Empleado empleado, Finca finca, LocalDate inicio, LocalDate fin);

    Asignacion cerrarAsignacion(Long asignacionId, LocalDate fechaFin);

    void eliminarAsignacion(Long asignacionId);

    void guardar(Asignacion asignacion);

    List<Asignacion> encontrarPorEmpleadoId(Long empleadoId);




}
