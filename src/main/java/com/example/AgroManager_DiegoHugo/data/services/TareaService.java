package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.model.Tarea;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TareaService {

    List<Tarea> encontrarTodas();

    Optional<Tarea> encontrarPorId(Long id);

    List<Tarea> tareasDeEmpleado(Long empleadoId);

    List<Tarea> tareasDeFinca(Long fincaId);

    List<Tarea> tareasDeEmpleadoEntreFechas(Long empleadoId, LocalDate inicio, LocalDate fin);

    BigDecimal horasTrabajadasEntreFechas(Long empleadoId, LocalDate inicio, LocalDate fin);

    Tarea registrarTarea(Empleado empleado, Finca finca, LocalDate fecha, String tipo, BigDecimal horas);

    void eliminarPorId(Long id);

    List<Tarea> todasLasTareas();

    Tarea marcarRealizada(Long tareaId);


}
