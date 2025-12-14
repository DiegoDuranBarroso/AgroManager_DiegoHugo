package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.*;
import com.example.AgroManager_DiegoHugo.data.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CerrarAsignacionTest {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private GerenteRepository gerenteRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private FincaRepository fincaRepository;
    @Autowired private AsignacionRepository asignacionRepository;

    @Test
    void cerrarAsignacion_setFechaFinYActivaFalse() {
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

        Asignacion a = new Asignacion();
        a.setFechaInicio(LocalDate.now().minusDays(3));
        a.setFechaFin(null);
        a.setActiva(true);
        a.setEmpleado(e);
        a.setFinca(f);
        a = asignacionRepository.save(a);

        // Cerrar asignación
        LocalDate fin = LocalDate.now();
        a.setFechaFin(fin);
        a.setActiva(false);
        asignacionRepository.save(a);

        Asignacion rec = asignacionRepository.findById(a.getId()).orElseThrow();
        assertEquals(fin, rec.getFechaFin());
        assertFalse(rec.getActiva());
    }
}
