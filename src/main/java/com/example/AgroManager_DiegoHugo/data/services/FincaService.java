package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.EstadoFinca;
import com.example.AgroManager_DiegoHugo.data.model.Finca;

import java.util.List;
import java.util.Optional;

public interface FincaService {

    List<Finca> encontrarTodas();

    Optional<Finca> encontrarPorId(Long id);

    List<Finca> encontrarPorGerente(Long gerenteId);

    List<Finca> encontrarPorEstado(EstadoFinca estado);

    List<Finca> buscarPorNombre(String nombre);

    Finca guardar(Finca finca);

    void eliminarPorId(Long id);

    Finca cambiarEstado(Long fincaId, EstadoFinca nuevoEstado);
}
