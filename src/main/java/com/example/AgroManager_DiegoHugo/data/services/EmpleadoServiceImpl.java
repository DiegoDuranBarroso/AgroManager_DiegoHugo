package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.repositories.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    @Autowired
    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> encontrarTodos() {
        return empleadoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> encontrarActivos() {
        return empleadoRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> encontrarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido: " + id);
        }
        return empleadoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> encontrarPorDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }
        return empleadoRepository.findByDni(dni.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return empleadoRepository.findAll();
        }
        return empleadoRepository.findByNombreContainingIgnoreCase(nombre.trim());
    }

    @Override
    public Empleado guardar(Empleado empleado) {
        if (empleado == null) {
            throw new IllegalArgumentException("El empleado no puede ser nulo");
        }
        if (empleado.getDni() == null || empleado.getDni().trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI del empleado es obligatorio");
        }
        if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del empleado es obligatorio");
        }
        if (empleado.getUsuario() == null) {
            throw new IllegalArgumentException("El empleado debe estar asociado a un usuario");
        }

        return empleadoRepository.save(empleado);
    }

    @Override
    public void eliminarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido: " + id);
        }
        if (!empleadoRepository.existsById(id)) {
            throw new IllegalArgumentException("Empleado con ID " + id + " no encontrado");
        }
        empleadoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> encontrarPorUsuarioId(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido: " + usuarioId);
        }
        return empleadoRepository.findByUsuarioId(usuarioId);
    }

}
