package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.*;
import com.example.AgroManager_DiegoHugo.data.repositories.GerenteRepository;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
import com.example.AgroManager_DiegoHugo.data.services.FincaService;
import com.example.AgroManager_DiegoHugo.data.services.TareaService;
import com.example.AgroManager_DiegoHugo.data.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class VerListaTareasTest {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private FincaService fincaService;

    @Autowired
    private TareaService tareaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private GerenteRepository gerenteRepository;

    // ---------- HELPERS ----------

    private Gerente crearGerenteTest(String sufijo) {
        Usuario u = new Usuario();
        u.setUsername("gerenteTareas_" + sufijo);
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
        // Usuario asociado (requisito del servicio)
        Usuario u = new Usuario();
        u.setUsername("empleadoTareas_" + sufijo);
        u.setPasswordHash("{noop}pass");
        u.setRol(Rol.EMPLEADO);
        u.setActivo(true);
        u = usuarioService.guardar(u);

        Empleado e = new Empleado();
        e.setNombre("Empleado " + sufijo);
        e.setDni("0000000" + sufijo + "A"); // DNI cualquiera pero no nulo/ni vacío
        e.setActivo(true);
        e.setUsuario(u);

        return empleadoService.guardar(e);
    }

    // ---------- TEST 1: gerente ve todas y cada empleado solo las suyas ----------

    @Test
    void gerenteVeTodasLasTareasYCadaEmpleadoVeSoloLasSuyas() {
        // Gerente y finca
        Gerente gerente = crearGerenteTest("lista");
        Finca finca = new Finca();
        finca.setNombre("Finca Tareas");
        finca.setEstado(EstadoFinca.SEMBRADA);
        finca.setCiudad("Ciudad X");
        finca.setProvincia("Provincia X");
        finca.setArea(10.0);
        finca.setGerente(gerente);
        finca = fincaService.guardar(finca);

        // Dos empleados con DNI + usuario
        Empleado e1 = crearEmpleadoTest("1", gerente);
        Empleado e2 = crearEmpleadoTest("2", gerente);

        // Tareas
        tareaService.registrarTarea(e1, finca, LocalDate.now(), "Poda", new BigDecimal("3"));
        tareaService.registrarTarea(e2, finca, LocalDate.now(), "Riego", new BigDecimal("4"));

        // Como GERENTE → todas las tareas
        List<Tarea> todas = tareaService.encontrarTodas();
        assertTrue(todas.size() >= 2, "El gerente debería ver al menos las 2 tareas creadas");

        // Como EMPLEADO → solo sus tareas
        List<Tarea> tareasE1 = tareaService.tareasDeEmpleado(e1.getId());
        List<Tarea> tareasE2 = tareaService.tareasDeEmpleado(e2.getId());

        assertEquals(1, tareasE1.size(), "Empleado 1 solo debería tener 1 tarea");
        assertEquals("Poda", tareasE1.get(0).getTipo());

        assertEquals(1, tareasE2.size(), "Empleado 2 solo debería tener 1 tarea");
        assertEquals("Riego", tareasE2.get(0).getTipo());
    }

    // ---------- TEST 2: caso de uso empleado ve su lista de tareas ----------

    @Test
    void empleadoPuedeVerSuListaDeTareas() {
        Gerente gerente = crearGerenteTest("empleadoLista");
        Finca finca = new Finca();
        finca.setNombre("Finca Empleado Lista");
        finca.setEstado(EstadoFinca.SEMBRADA);
        finca.setCiudad("Ciudad Y");
        finca.setProvincia("Provincia Y");
        finca.setArea(5.0);
        finca.setGerente(gerente);
        finca = fincaService.guardar(finca);

        Empleado empleado = crearEmpleadoTest("Solo", gerente);

        // Tres tareas para este empleado
        tareaService.registrarTarea(empleado, finca, LocalDate.now(), "Riego", new BigDecimal("2"));
        tareaService.registrarTarea(empleado, finca, LocalDate.now(), "Poda", new BigDecimal("3"));
        tareaService.registrarTarea(empleado, finca, LocalDate.now(), "Abonado", new BigDecimal("4"));

        // Simulamos la consulta que haría el empleado desde la opción "Ver mis tareas"
        List<Tarea> misTareas = tareaService.tareasDeEmpleado(empleado.getId());

        assertEquals(3, misTareas.size(), "El empleado debería ver exactamente sus 3 tareas");
        assertTrue(
                misTareas.stream().allMatch(t -> t.getEmpleado().getId().equals(empleado.getId())),
                "Todas las tareas devueltas deben pertenecer al empleado"
        );
    }
}
