package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.Asignacion;
import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.repositories.AsignacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AsignacionServiceImpl implements AsignacionService {

    private final AsignacionRepository asignacionRepository;

    @Autowired
    public AsignacionServiceImpl(AsignacionRepository asignacionRepository) {
        this.asignacionRepository = asignacionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asignacion> encontrarTodas() {
        return asignacionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Asignacion> encontrarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de asignación inválido: " + id);
        }
        return asignacionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asignacion> encontrarPorEmpleado(Long empleadoId) {
        if (empleadoId == null || empleadoId <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido: " + empleadoId);
        }
        return asignacionRepository.findByEmpleadoId(empleadoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asignacion> encontrarPorFinca(Long fincaId) {
        if (fincaId == null || fincaId <= 0) {
            throw new IllegalArgumentException("ID de finca inválido: " + fincaId);
        }
        return asignacionRepository.findByFincaId(fincaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asignacion> encontrarActivas() {
        return asignacionRepository.findByActivaTrue();
    }

    @Override
    public Asignacion asignarEmpleadoAFinca(Empleado empleado, Finca finca, LocalDate inicio, LocalDate fin) {
        if (empleado == null || empleado.getId() == null) {
            throw new IllegalArgumentException("Empleado inválido");
        }
        if (finca == null || finca.getId() == null) {
            throw new IllegalArgumentException("Finca inválida");
        }
        if (inicio == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        }
        if (fin != null && fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior al inicio");
        }

        // Tope de fecha para las consultas (evitamos LocalDate.MAX con MySQL)
        LocalDate finConsulta = (fin != null) ? fin : LocalDate.of(9999, 12, 31);

        boolean solapa = asignacionRepository.existeSolapamiento(
                empleado.getId(),
                inicio,
                finConsulta
        );
        if (solapa) {
            throw new IllegalStateException("El empleado ya tiene una asignación activa que solapa con estas fechas");
        }

        LocalDate hoy = LocalDate.now();
        boolean activa = true;

        if (fin != null && fin.isBefore(hoy) && inicio.isBefore(hoy)) {
            activa = false;  // asignación histórica, ya cerrada
        }

        Asignacion asignacion = new Asignacion(inicio, fin, activa, empleado, finca);
        return asignacionRepository.save(asignacion);
    }



    @Override
    public Asignacion cerrarAsignacion(Long asignacionId, LocalDate fechaFin) {
        Asignacion asignacion = asignacionRepository.findById(asignacionId)
                .orElseThrow(() -> new IllegalArgumentException("Asignación con ID " + asignacionId + " no encontrada"));

        if (asignacion.getActiva() != null && !asignacion.getActiva()) {
            return asignacion;
        }

        LocalDate fechaCierre = (fechaFin != null) ? fechaFin : LocalDate.now();


        if (fechaCierre.isBefore(asignacion.getFechaInicio())) {
            fechaCierre = asignacion.getFechaInicio();
        }

        asignacion.setFechaFin(fechaCierre);
        asignacion.setActiva(false);

        return asignacionRepository.save(asignacion);
    }

    @Override
    public void eliminarAsignacion(Long asignacionId) {
        if (asignacionId == null || asignacionId <= 0) {
            throw new IllegalArgumentException("ID de asignación inválido: " + asignacionId);
        }

        if (!asignacionRepository.existsById(asignacionId)) {
            throw new IllegalArgumentException("Asignación con ID " + asignacionId + " no encontrada");
        }

        asignacionRepository.deleteById(asignacionId);
    }

}
