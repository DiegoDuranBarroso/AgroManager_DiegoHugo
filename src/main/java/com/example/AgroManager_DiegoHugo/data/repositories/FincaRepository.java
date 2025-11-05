package com.example.AgroManager_DiegoHugo.data.repositories;

import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.model.EstadoFinca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FincaRepository extends JpaRepository<Finca, Long> {
    List<Finca> findByGerenteId(Long gerenteId);
    List<Finca> findByEstado(EstadoFinca estado);
    List<Finca> findByNombreContainingIgnoreCase(String nombre);
}
