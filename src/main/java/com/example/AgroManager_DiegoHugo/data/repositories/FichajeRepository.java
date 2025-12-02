package com.example.AgroManager_DiegoHugo.data.repositories;

import com.example.AgroManager_DiegoHugo.data.model.Fichaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FichajeRepository extends JpaRepository<Fichaje, Long> {
    Optional<Fichaje> findFirstByEmpleadoIdAndFinIsNullOrderByInicioDesc(Long empleadoId);
    List<Fichaje> findByEmpleadoId(Long empleadoId);
    List<Fichaje> findByInicioBetween(Instant desde, Instant hasta);
    List<Fichaje> findByEmpleadoIdAndInicioBetween(Long empleadoId, Instant desde, Instant hasta);
    List<Fichaje> findByFincaId(Long fincaId);

    void deleteByFincaId(Long fincaId);
}
