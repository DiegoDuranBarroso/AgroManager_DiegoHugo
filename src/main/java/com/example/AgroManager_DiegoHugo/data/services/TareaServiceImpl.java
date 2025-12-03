package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.model.Tarea;
import com.example.AgroManager_DiegoHugo.data.repositories.TareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TareaServiceImpl implements TareaService {

    private final TareaRepository tareaRepository;

    @Autowired
    public TareaServiceImpl(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tarea> encontrarTodas() {
        return tareaRepository.findAll();
    }

    /**
     * Método de conveniencia para el controlador.
     * Equivalente a {@link #encontrarTodas()}.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Tarea> todasLasTareas() {
        return tareaRepository.findAll();
        // o si prefieres:
        // return encontrarTodas();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tarea> encontrarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de tarea inválido: " + id);
        }
        return tareaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tarea> tareasDeEmpleado(Long empleadoId) {
        if (empleadoId == null || empleadoId <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido: " + empleadoId);
        }
        return tareaRepository.findByEmpleadoId(empleadoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tarea> tareasDeFinca(Long fincaId) {
        if (fincaId == null || fincaId <= 0) {
            throw new IllegalArgumentException("ID de finca inválido: " + fincaId);
        }
        return tareaRepository.findByFincaId(fincaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tarea> tareasDeEmpleadoEntreFechas(Long empleadoId, LocalDate inicio, LocalDate fin) {
        if (empleadoId == null || empleadoId <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido: " + empleadoId);
        }
        if (inicio == null || fin == null || fin.isBefore(inicio)) {
            throw new IllegalArgumentException("Rango de fechas inválido");
        }
        return tareaRepository.findByEmpleadoIdAndFechaBetween(empleadoId, inicio, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal horasTrabajadasEntreFechas(Long empleadoId, LocalDate inicio, LocalDate fin) {
        if (empleadoId == null || empleadoId <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido: " + empleadoId);
        }
        if (inicio == null || fin == null || fin.isBefore(inicio)) {
            throw new IllegalArgumentException("Rango de fechas inválido");
        }
        return tareaRepository.sumHoras(empleadoId, inicio, fin);
    }

    @Override
    public Tarea registrarTarea(Empleado empleado, Finca finca, LocalDate fecha, String tipo, BigDecimal horas) {
        if (empleado == null || empleado.getId() == null) {
            throw new IllegalArgumentException("Empleado inválido");
        }
        if (finca == null || finca.getId() == null) {
            throw new IllegalArgumentException("Finca inválida");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de tarea es obligatorio");
        }
        if (horas == null || horas.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Las horas deben ser mayores que cero");
        }

        Tarea tarea = new Tarea(fecha, tipo.trim(), horas, empleado, finca);
        return tareaRepository.save(tarea);
    }

    @Override
    public void eliminarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de tarea inválido: " + id);
        }
        if (!tareaRepository.existsById(id)) {
            throw new IllegalArgumentException("Tarea con ID " + id + " no encontrada");
        }
        tareaRepository.deleteById(id);
    }

    @Override
    public Tarea marcarRealizada(Long tareaId) {

        Tarea tarea = tareaRepository.findById(tareaId)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));

        tarea.setRealizada(true);

        return tareaRepository.save(tarea);
    }

    // === NUEVO: GUARDAR TAREA (CREAR / EDITAR) ===
    @Override
    public Tarea guardar(Tarea tarea) {
        if (tarea == null) {
            throw new IllegalArgumentException("La tarea no puede ser nula");
        }

        if (tarea.getEmpleado() == null || tarea.getEmpleado().getId() == null) {
            throw new IllegalArgumentException("Empleado inválido en la tarea");
        }

        if (tarea.getFinca() == null || tarea.getFinca().getId() == null) {
            throw new IllegalArgumentException("Finca inválida en la tarea");
        }

        if (tarea.getFecha() == null) {
            throw new IllegalArgumentException("La fecha de la tarea es obligatoria");
        }

        if (tarea.getTipo() == null || tarea.getTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de tarea es obligatorio");
        }

        if (tarea.getHoras() == null || tarea.getHoras().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Las horas deben ser mayores que cero");
        }

        // Si supera las validaciones, se guarda (crea o actualiza según si tiene id)
        return tareaRepository.save(tarea);
    }
}
