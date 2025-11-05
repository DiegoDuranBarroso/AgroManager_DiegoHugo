package com.example.AgroManager_DiegoHugo.data.repositories;

import com.example.AgroManager_DiegoHugo.data.model.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    List<Asignacion> findByEmpleadoId(Long empleadoId);
    List<Asignacion> findByFincaId(Long fincaId);
    List<Asignacion> findByActivaTrue();

    @Query("""
        select case when count(a) > 0 then true else false end
        from Asignacion a
        where a.empleado.id = :empleadoId
          and a.activa = true
          and (a.fechaFin is null or a.fechaFin >= :inicio)
          and a.fechaInicio <= :fin
    """)
    boolean existeSolapamiento(Long empleadoId, LocalDate inicio, LocalDate fin);
}
