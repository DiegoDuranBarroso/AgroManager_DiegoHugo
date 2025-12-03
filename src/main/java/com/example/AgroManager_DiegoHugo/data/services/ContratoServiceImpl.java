package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.Contrato;
import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.TipoContrato;
import com.example.AgroManager_DiegoHugo.data.repositories.ContratoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContratoServiceImpl implements ContratoService {

    private final ContratoRepository contratoRepository;

    @Autowired
    public ContratoServiceImpl(ContratoRepository contratoRepository) {
        this.contratoRepository = contratoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrato> encontrarTodos() {
        return contratoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Contrato> encontrarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de contrato inválido: " + id);
        }
        return contratoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrato> encontrarPorEmpleado(Long empleadoId) {
        if (empleadoId == null || empleadoId <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido: " + empleadoId);
        }
        return contratoRepository.findByEmpleadoId(empleadoId, Sort.by(Sort.Direction.DESC, "fechaInicio"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrato> contratosVigentesEn(Long empleadoId, LocalDate fecha) {
        if (empleadoId == null || empleadoId <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido: " + empleadoId);
        }
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        return contratoRepository.findVigentesEn(empleadoId, fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Contrato> ultimoContratoDeEmpleado(Long empleadoId) {
        List<Contrato> contratos = contratoRepository.findByEmpleadoIdOrderByInicioDesc(empleadoId);
        if (contratos.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(contratos.get(0));
    }

    @Override
    public Contrato crearContrato(Empleado empleado,
                                  TipoContrato tipo,
                                  LocalDate fechaInicio,
                                  LocalDate fechaFin,
                                  BigDecimal salarioBase,
                                  BigDecimal tarifaHora) {
        if (empleado == null || empleado.getId() == null) {
            throw new IllegalArgumentException("Empleado inválido");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de contrato es obligatorio");
        }
        if (fechaInicio == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        }
        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
        }
        if (salarioBase == null || salarioBase.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El salario base no puede ser negativo");
        }
        if (tarifaHora == null || tarifaHora.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La tarifa por hora no puede ser negativa");
        }

        Contrato contrato = new Contrato(tipo, fechaInicio, fechaFin, salarioBase, tarifaHora, empleado);
        return contratoRepository.save(contrato);
    }

    @Override
    public void eliminarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de contrato inválido: " + id);
        }
        if (!contratoRepository.existsById(id)) {
            throw new IllegalArgumentException("Contrato con ID " + id + " no encontrado");
        }
        contratoRepository.deleteById(id);
    }

    @Override
    public void guardar(Contrato contrato) {
        if (contrato == null) {
            throw new IllegalArgumentException("El contrato no puede ser nulo");
        }
        if (contrato.getEmpleado() == null || contrato.getEmpleado().getId() == null) {
            throw new IllegalArgumentException("El contrato debe tener un empleado con id");
        }
        if (contrato.getTipo() == null) {
            throw new IllegalArgumentException("El contrato debe tener un tipo");
        }
        if (contrato.getFechaInicio() == null) {
            throw new IllegalArgumentException("El contrato debe tener una fecha de inicio");
        }
        if (contrato.getFechaFin() != null && contrato.getFechaFin().isBefore(contrato.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        // Persistir (crea o actualiza)
        contratoRepository.save(contrato);
    }
}
