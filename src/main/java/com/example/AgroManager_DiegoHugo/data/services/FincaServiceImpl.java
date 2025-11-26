package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.EstadoFinca;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.repositories.FincaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FincaServiceImpl implements FincaService {

    private final FincaRepository fincaRepository;

    @Autowired
    public FincaServiceImpl(FincaRepository fincaRepository) {
        this.fincaRepository = fincaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Finca> encontrarTodas() {
        return fincaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Finca> encontrarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de finca inválido: " + id);
        }
        return fincaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Finca> encontrarPorGerente(Long gerenteId) {
        if (gerenteId == null || gerenteId <= 0) {
            throw new IllegalArgumentException("ID de gerente inválido: " + gerenteId);
        }
        return fincaRepository.findByGerenteId(gerenteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Finca> encontrarPorEstado(EstadoFinca estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado de la finca no puede ser nulo");
        }
        return fincaRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Finca> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return fincaRepository.findAll();
        }
        return fincaRepository.findByNombreContainingIgnoreCase(nombre.trim());
    }

    @Override
    public Finca guardar(Finca finca) {
        if (finca == null) {
            throw new IllegalArgumentException("La finca no puede ser nula");
        }
        if (finca.getNombre() == null || finca.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la finca es obligatorio");
        }
        if (finca.getGerente() == null || finca.getGerente().getId() == null) {
            throw new IllegalArgumentException("La finca debe estar asociada a un gerente");
        }
        if (finca.getEstado() == null) {
            throw new IllegalArgumentException("El estado de la finca es obligatorio");
        }
        return fincaRepository.save(finca);
    }

    @Override
    public void eliminarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de finca inválido: " + id);
        }
        if (!fincaRepository.existsById(id)) {
            throw new IllegalArgumentException("Finca con ID " + id + " no encontrada");
        }
        fincaRepository.deleteById(id);
    }

    @Override
    public Finca cambiarEstado(Long fincaId, EstadoFinca nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
        }
        Finca finca = fincaRepository.findById(fincaId)
                .orElseThrow(() -> new IllegalArgumentException("Finca con ID " + fincaId + " no encontrada"));
        finca.setEstado(nuevoEstado);
        return fincaRepository.save(finca);
    }
}
