package com.example.AgroManager_DiegoHugo.data.repositories;

import com.example.AgroManager_DiegoHugo.data.model.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {
    default List<Contrato> findByEmpleadoIdOrderByInicioDesc(Long empleadoId) {
        return findByEmpleadoId(empleadoId, Sort.by(Sort.Direction.DESC, "fechaInicio"));
    }

    List<Contrato> findByEmpleadoId(Long empleadoId, Sort sort);

    @Query("""
        select c from Contrato c
        where c.empleado.id = :empleadoId
          and c.fechaInicio <= :fecha
          and (c.fechaFin is null or c.fechaFin >= :fecha)
    """)
    List<Contrato> findVigentesEn(Long empleadoId, LocalDate fecha);
}
