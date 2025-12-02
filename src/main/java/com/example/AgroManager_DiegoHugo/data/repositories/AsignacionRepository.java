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
                SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END
                FROM Asignacion a
                WHERE a.empleado.id = :empleadoId
                  AND a.activa = TRUE
                  AND (a.fechaFin IS NULL OR a.fechaFin >= :fechaInicio)
                  AND a.fechaInicio <= :fechaFin
            """)
    boolean existeSolapamiento(Long empleadoId,
                               LocalDate fechaInicio,
                               LocalDate fechaFin);
    void deleteByFincaId(Long fincaId);

}
