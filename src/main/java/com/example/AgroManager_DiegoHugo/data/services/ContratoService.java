package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.Contrato;
import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.TipoContrato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContratoService {

    List<Contrato> encontrarTodos();

    Optional<Contrato> encontrarPorId(Long id);

    List<Contrato> encontrarPorEmpleado(Long empleadoId);

    List<Contrato> contratosVigentesEn(Long empleadoId, LocalDate fecha);

    Optional<Contrato> ultimoContratoDeEmpleado(Long empleadoId);

    Contrato crearContrato(Empleado empleado,
                           TipoContrato tipo,
                           LocalDate fechaInicio,
                           LocalDate fechaFin,
                           BigDecimal salarioBase,
                           BigDecimal tarifaHora);

    void eliminarPorId(Long id);

    void guardar(Contrato contrato);
}
