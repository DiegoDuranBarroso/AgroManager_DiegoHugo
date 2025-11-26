package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Nomina;
import com.example.AgroManager_DiegoHugo.data.repositories.NominaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NominaServiceImpl implements NominaService {

    private final NominaRepository nominaRepository;

    @Autowired
    public NominaServiceImpl(NominaRepository nominaRepository) {
        this.nominaRepository = nominaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Nomina> encontrarTodas() {
        return nominaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Nomina> encontrarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de nómina inválido: " + id);
        }
        return nominaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Nomina> nominasDeEmpleado(Long empleadoId) {
        if (empleadoId == null || empleadoId <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido: " + empleadoId);
        }
        return nominaRepository.findByEmpleadoIdOrderByPeriodoInicioDesc(empleadoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Nomina> nominasPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede estar vacío");
        }
        return nominaRepository.findByEstado(estado.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Nomina> nominasEnFecha(Long empleadoId, LocalDate fecha) {
        if (empleadoId == null || empleadoId <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido: " + empleadoId);
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        return nominaRepository.findEnFecha(empleadoId, fecha);
    }

    @Override
    public Nomina generarNomina(Empleado empleado,
                                LocalDate periodoInicio,
                                LocalDate periodoFin,
                                BigDecimal totalBruto,
                                String estado) {
        if (empleado == null || empleado.getId() == null) {
            throw new IllegalArgumentException("Empleado inválido");
        }
        if (periodoInicio == null || periodoFin == null || periodoFin.isBefore(periodoInicio)) {
            throw new IllegalArgumentException("Periodo de nómina inválido");
        }
        if (totalBruto == null || totalBruto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El total bruto no puede ser negativo");
        }
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado de la nómina es obligatorio");
        }

        Nomina nomina = new Nomina(periodoInicio, periodoFin, totalBruto, estado.trim(), empleado);
        return nominaRepository.save(nomina);
    }

    @Override
    public void eliminarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de nómina inválido: " + id);
        }
        if (!nominaRepository.existsById(id)) {
            throw new IllegalArgumentException("Nómina con ID " + id + " no encontrada");
        }
        nominaRepository.deleteById(id);
    }
}
