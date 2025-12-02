package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.EstadoFinca;
import com.example.AgroManager_DiegoHugo.data.model.Fichaje;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.repositories.AsignacionRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.FichajeRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.FincaRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.TareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FincaServiceImpl implements FincaService {

    private final FincaRepository fincaRepository;
    private final AsignacionRepository asignacionRepository;
    private final TareaRepository tareaRepository;
    private final FichajeRepository fichajeRepository;

    @Autowired
    public FincaServiceImpl(FincaRepository fincaRepository,
                            AsignacionRepository asignacionRepository,
                            TareaRepository tareaRepository,
                            FichajeRepository fichajeRepository) {
        this.fincaRepository = fincaRepository;
        this.asignacionRepository = asignacionRepository;
        this.tareaRepository = tareaRepository;
        this.fichajeRepository = fichajeRepository;
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

        Finca finca = fincaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Finca con ID " + id + " no encontrada"));

        // 1) Cerrar fichajes abiertos de esa finca y luego borrar todos
        List<Fichaje> fichajes = fichajeRepository.findByFincaId(id);
        for (Fichaje f : fichajes) {
            if (f.getFin() == null) {
                f.setFin(Instant.now());
                f.setEstado("CERRADO"); // adapta si usas enum
                fichajeRepository.save(f);
            }
        }
        fichajeRepository.deleteByFincaId(id);

        // 2) Borrar tareas de esa finca
        tareaRepository.deleteByFincaId(id);

        // 3) Borrar asignaciones de esa finca
        asignacionRepository.deleteByFincaId(id);

        // 4) Borrar la finca
        fincaRepository.delete(finca);
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
