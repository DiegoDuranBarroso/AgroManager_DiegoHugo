package com.example.AgroManager_DiegoHugo.data.repositories;

import com.example.AgroManager_DiegoHugo.data.model.Nomina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface NominaRepository extends JpaRepository<Nomina, Long> {
    List<Nomina> findByEmpleadoIdOrderByPeriodoInicioDesc(Long empleadoId);
    List<Nomina> findByEstado(String estado);

    @Query("""
        select n from Nomina n
        where n.empleado.id = :empleadoId
          and :fecha between n.periodoInicio and n.periodoFin
    """)
    List<Nomina> findEnFecha(Long empleadoId, LocalDate fecha);
}
