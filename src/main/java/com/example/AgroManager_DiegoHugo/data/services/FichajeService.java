package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Fichaje;
import com.example.AgroManager_DiegoHugo.data.model.Finca;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FichajeService {

    List<Fichaje> fichajesDeEmpleado(Long empleadoId);

    List<Fichaje> fichajesEntre(Instant desde, Instant hasta);

    List<Fichaje> fichajesDeEmpleadoEntre(Long empleadoId, Instant desde, Instant hasta);

    Optional<Fichaje> fichajeAbierto(Long empleadoId);

    Fichaje iniciarFichaje(Empleado empleado, Finca finca);

    Fichaje finalizarFichaje(Long empleadoId);

    List<Fichaje> fichajesTodos();

    Optional<Fichaje> fichajePorId(Long id);

    void eliminarPorId(Long id);

}
