package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Nomina;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NominaService {

    List<Nomina> encontrarTodas();

    Optional<Nomina> encontrarPorId(Long id);

    List<Nomina> nominasDeEmpleado(Long empleadoId);

    List<Nomina> nominasPorEstado(String estado);

    List<Nomina> nominasEnFecha(Long empleadoId, LocalDate fecha);

    Nomina generarNomina(Empleado empleado,
                         LocalDate periodoInicio,
                         LocalDate periodoFin,
                         BigDecimal totalBruto,
                         String estado);

    void eliminarPorId(Long id);
}
