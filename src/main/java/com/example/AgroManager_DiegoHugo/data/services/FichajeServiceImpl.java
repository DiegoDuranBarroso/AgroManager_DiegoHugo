package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Fichaje;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.repositories.FichajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FichajeServiceImpl implements FichajeService {

    private final FichajeRepository fichajeRepository;

    @Autowired
    public FichajeServiceImpl(FichajeRepository fichajeRepository) {
        this.fichajeRepository = fichajeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fichaje> fichajesDeEmpleado(Long empleadoId) {
        if (empleadoId == null || empleadoId <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido: " + empleadoId);
        }
        return fichajeRepository.findByEmpleadoId(empleadoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fichaje> fichajesEntre(Instant desde, Instant hasta) {
        if (desde == null || hasta == null || !desde.isBefore(hasta)) {
            throw new IllegalArgumentException("Rango de fechas/hora inválido");
        }
        return fichajeRepository.findByInicioBetween(desde, hasta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fichaje> fichajesDeEmpleadoEntre(Long empleadoId, Instant desde, Instant hasta) {
        if (empleadoId == null || empleadoId <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido: " + empleadoId);
        }
        if (desde == null || hasta == null || !desde.isBefore(hasta)) {
            throw new IllegalArgumentException("Rango de fechas/hora inválido");
        }
        return fichajeRepository.findByEmpleadoIdAndInicioBetween(empleadoId, desde, hasta);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Fichaje> fichajeAbierto(Long empleadoId) {
        if (empleadoId == null || empleadoId <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido: " + empleadoId);
        }
        return fichajeRepository.findFirstByEmpleadoIdAndFinIsNullOrderByInicioDesc(empleadoId);
    }

    @Override
    public Fichaje iniciarFichaje(Empleado empleado, Finca finca) {
        if (empleado == null || empleado.getId() == null) {
            throw new IllegalArgumentException("Empleado inválido");
        }
        if (finca == null || finca.getId() == null) {
            throw new IllegalArgumentException("Finca inválida");
        }

        Optional<Fichaje> abierto = fichajeRepository
                .findFirstByEmpleadoIdAndFinIsNullOrderByInicioDesc(empleado.getId());
        if (abierto.isPresent()) {
            throw new IllegalStateException("El empleado ya tiene un fichaje abierto");
        }

        Fichaje fichaje = new Fichaje(Instant.now(), null, "ABIERTO", empleado, finca);
        return fichajeRepository.save(fichaje);
    }

    @Override
    public Fichaje finalizarFichaje(Long empleadoId) {
        if (empleadoId == null || empleadoId <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido: " + empleadoId);
        }

        Fichaje fichaje = fichajeRepository
                .findFirstByEmpleadoIdAndFinIsNullOrderByInicioDesc(empleadoId)
                .orElseThrow(() -> new IllegalStateException("No hay fichaje abierto para este empleado"));

        fichaje.setFin(Instant.now());
        fichaje.setEstado("CERRADO");
        return fichajeRepository.save(fichaje);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fichaje> fichajesTodos() {
        return fichajeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Fichaje> fichajePorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de fichaje inválido: " + id);
        }
        return fichajeRepository.findById(id);
    }

    @Override
    public void eliminarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de fichaje inválido: " + id);
        }
        fichajeRepository.deleteById(id);
    }
}
