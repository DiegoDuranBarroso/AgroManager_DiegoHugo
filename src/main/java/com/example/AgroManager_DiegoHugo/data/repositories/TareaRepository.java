package com.example.AgroManager_DiegoHugo.data.repositories;

import com.example.AgroManager_DiegoHugo.data.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    List<Tarea> findByEmpleadoId(Long empleadoId);
    List<Tarea> findByFincaId(Long fincaId);
    List<Tarea> findByEmpleadoIdAndFechaBetween(Long empleadoId, LocalDate inicio, LocalDate fin);

    @Query("""
        select coalesce(sum(t.horas), 0)
        from Tarea t
        where t.empleado.id = :empleadoId
          and t.fecha between :inicio and :fin
    """)
    BigDecimal sumHoras(Long empleadoId, LocalDate inicio, LocalDate fin);

    void deleteByFincaId(Long fincaId);
}
