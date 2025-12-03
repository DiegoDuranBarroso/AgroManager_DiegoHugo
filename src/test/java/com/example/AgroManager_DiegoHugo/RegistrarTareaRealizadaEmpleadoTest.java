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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class RegistrarTareaRealizadaEmpleadoTest {

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

    // ==== Helpers ===========================================================

    private Gerente crearGerenteTest(String sufijo) {
        Usuario u = new Usuario();
        u.setUsername("gerenteTareaRealizada_" + sufijo);
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

    private Empleado crearEmpleadoTest(String sufijo) {
        // Usuario del empleado
        Usuario u = new Usuario();
        u.setUsername("empleadoTareaRealizada_" + sufijo);
        u.setPasswordHash("{noop}pass");
        u.setRol(Rol.EMPLEADO);
        u.setActivo(true);
        u = usuarioService.guardar(u);

        // Empleado asociado al usuario
        Empleado emp = new Empleado();
        emp.setNombre("Empleado Tarea Realizada " + sufijo);
        emp.setDni("0000000" + sufijo + "A");   // DNI obligatorio en tu servicio
        emp.setActivo(true);
        emp.setUsuario(u);                      // ASOCIAR USUARIO (clave del error)
        return empleadoService.guardar(emp);
    }

    // ==== Test =============================================================

    @Test
    void empleadoPuedeMarcarTareaComoRealizada() {
        // Empleado correctamente creado
        Empleado emp = crearEmpleadoTest("1");

        // Gerente y finca
        Gerente gerente = crearGerenteTest("realizada");

        Finca finca = new Finca();
        finca.setNombre("Finca Tarea Realizada");
        finca.setEstado(EstadoFinca.SEMBRADA);
        finca.setCiudad("Ciudad C");
        finca.setProvincia("Provincia C");
        finca.setArea(12.0);
        finca.setGerente(gerente);
        finca = fincaService.guardar(finca);

        // Tarea inicialmente pendiente
        Tarea tarea = tareaService.registrarTarea(
                emp,
                finca,
                LocalDate.now(),
                "Recolección",
                new BigDecimal("6")
        );

        assertFalse(tarea.isRealizada(), "La tarea recién creada debe estar pendiente");

        // Marcar como realizada
        Tarea realizada = tareaService.marcarRealizada(tarea.getId());

        assertTrue(realizada.isRealizada(), "La tarea debería pasar a realizada");
        assertNotNull(realizada.getFecha(),
                "La fecha de la tarea debería estar informada");
    }
}
