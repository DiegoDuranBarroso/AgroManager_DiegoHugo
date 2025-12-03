package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.*;
import com.example.AgroManager_DiegoHugo.data.repositories.AsignacionRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.GerenteRepository;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
import com.example.AgroManager_DiegoHugo.data.services.FincaService;
import com.example.AgroManager_DiegoHugo.data.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class EliminarAsignacionTest {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private FincaService fincaService;

    @Autowired
    private AsignacionRepository asignacionRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private GerenteRepository gerenteRepository;

    // ---------- HELPERS ----------

    private Gerente crearGerenteTest(String sufijo) {
        Usuario u = new Usuario();
        u.setUsername("gerenteAsig_" + sufijo);
        u.setPasswordHash("{noop}pass");
        u.setRol(Rol.GERENTE);
        u.setActivo(true);
        u = usuarioService.guardar(u);

        Gerente g = new Gerente();
        g.setNombre("Gerente " + sufijo);
        g.setEmail("g" + sufijo + "@test.com");
        g.setUsuario(u);
        return gerenteRepository.save(g);
    }

    private Empleado crearEmpleadoTest(String sufijo, Gerente gerente) {
        // Usuario asociado (obligatorio en EmpleadoServiceImpl)
        Usuario u = new Usuario();
        u.setUsername("empleadoAsig_" + sufijo);
        u.setPasswordHash("{noop}pass");
        u.setRol(Rol.EMPLEADO);
        u.setActivo(true);
        u = usuarioService.guardar(u);

        Empleado e = new Empleado();
        e.setNombre("Empleado Asig " + sufijo);
        e.setDni("1234567" + sufijo + "A");  // DNI cualquiera, pero no nulo/ni vacío
        e.setActivo(true);
        e.setUsuario(u);

        return empleadoService.guardar(e);
    }

    // ---------- TEST ----------

    @Test
    void gerentePuedeEliminarUnaAsignacion() {
        // 1) Creamos gerente y finca
        Gerente gerente = crearGerenteTest("delAsig");

        Finca finca = new Finca();
        finca.setNombre("Finca Asig");
        finca.setEstado(EstadoFinca.SEMBRADA);
        finca.setCiudad("Ciudad A");
        finca.setProvincia("Provincia A");
        finca.setArea(5.0);
        finca.setGerente(gerente);
        finca = fincaService.guardar(finca);

        // 2) Empleado válido (DNI + usuario + gerente)
        Empleado emp = crearEmpleadoTest("1", gerente);

        // 3) Creamos asignación
        Asignacion asig = new Asignacion();
        asig.setEmpleado(emp);
        asig.setFinca(finca);
        asig.setFechaInicio(LocalDate.now());
        asig = asignacionRepository.save(asig);

        Long id = asig.getId();
        assertNotNull(id, "La asignación debe tener ID al guardarse");

        // 4) Eliminamos la asignación
        asignacionRepository.deleteById(id);

        // 5) Comprobamos que ya no existe
        Optional<Asignacion> borrada = asignacionRepository.findById(id);
        assertTrue(borrada.isEmpty(), "La asignación debería haberse eliminado");
    }
}
