package com.example.AgroManager_DiegoHugo.data.repositories;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    Optional<Empleado> findByDni(String dni);
    List<Empleado> findByActivoTrue();
    List<Empleado> findByNombreContainingIgnoreCase(String nombre);
}
