package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.*;
import com.example.AgroManager_DiegoHugo.data.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class EliminarFichajesTest {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private GerenteRepository gerenteRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private FincaRepository fincaRepository;
    @Autowired private FichajeRepository fichajeRepository;

    @Test
    void eliminarFichaje_borraRegistro() {
        Usuario ug = new Usuario();
        ug.setUsername("ger_" + UUID.randomUUID());
        ug.setPasswordHash("hash");
        ug.setRol(Rol.GERENTE);
        ug.setActivo(true);
        ug = usuarioRepository.save(ug);

        Gerente g = new Gerente();
        g.setNombre("Gerente");
        g.setEmail("g@mail.com");
        g.setTelefono("600000000");
        g.setUsuario(ug);
        g = gerenteRepository.save(g);

        Finca f = new Finca();
        f.setNombre("Finca 1");
        f.setEstado(EstadoFinca.SEMBRADA);
        f.setGerente(g);
        f = fincaRepository.save(f);

        Usuario ue = new Usuario();
        ue.setUsername("emp_" + UUID.randomUUID());
        ue.setPasswordHash("hash");
        ue.setRol(Rol.EMPLEADO);
        ue.setActivo(true);
        ue = usuarioRepository.save(ue);

        Empleado e = new Empleado();
        e.setDni("12345678Z");
        e.setNombre("Empleado");
        e.setActivo(true);
        e.setUsuario(ue);
        e = empleadoRepository.save(e);

        Fichaje fich = new Fichaje();
        fich.setInicio(Instant.now());
        fich.setFin(null);
        fich.setEstado("ABIERTO");
        fich.setEmpleado(e);
        fich.setFinca(f);
        fich = fichajeRepository.save(fich);

        Long id = fich.getId();
        fichajeRepository.deleteById(id);

        assertTrue(fichajeRepository.findById(id).isEmpty());
    }
}
