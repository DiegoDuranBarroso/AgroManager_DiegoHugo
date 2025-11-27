package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;

import java.util.List;
import java.util.Optional;

public interface EmpleadoService {

    List<Empleado> encontrarTodos();

    List<Empleado> encontrarActivos();

    Optional<Empleado> encontrarPorId(Long id);

    Optional<Empleado> encontrarPorDni(String dni);

    List<Empleado> buscarPorNombre(String nombre);

    Empleado guardar(Empleado empleado);

    void eliminarPorId(Long id);

    Optional<Empleado> encontrarPorUsuarioId(Long usuarioId);
}
