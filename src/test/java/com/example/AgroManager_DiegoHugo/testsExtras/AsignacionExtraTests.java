package com.example.AgroManager_DiegoHugo.testsExtras;

import com.example.AgroManager_DiegoHugo.data.repositories.*;
import com.example.AgroManager_DiegoHugo.data.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class AsignacionExtraTests {

    @Autowired
    AsignacionRepository asignacionRepo;
    @Autowired
    EmpleadoRepository empleadoRepo;
    @Autowired
    FincaRepository fincaRepo;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void noDebeHaberSolape_enRangoFuera() {
        var emp = empleadoRepo.findAll().get(0);
        LocalDate inicio = LocalDate.now().minusYears(1);
        LocalDate fin = LocalDate.now().minusYears(1).plusDays(10);
        boolean solapa = asignacionRepo.existeSolapamiento(emp.getId(), inicio, fin);
        assertThat(solapa).isFalse();
    }

    @Test
    void marcarAsignacionInactiva_permiteNuevaEnMismoRango() {
        // 👇 Creamos un empleado “limpio” sin asignaciones previas
        Usuario u = new Usuario("emp_" + UUID.randomUUID(), "hash", Rol.EMPLEADO, true);
        usuarioRepository.save(u);
        Empleado emp = empleadoRepo.save(new Empleado("X" + System.nanoTime(), "Test Emp", true, u));

        // Podemos reutilizar una finca existente
        Finca finca = fincaRepo.findAll().get(0);

        LocalDate i1 = LocalDate.now().minusDays(5);
        LocalDate f1 = LocalDate.now().plusDays(5);

        // Creamos una asignación activa que solapa
        var a1 = asignacionRepo.save(new Asignacion(i1, f1, true, emp, finca));
        assertThat(asignacionRepo.existeSolapamiento(emp.getId(), i1, f1)).isTrue();

        // La desactivamos y FLUSH para sincronizar con la BD
        a1.setActiva(false);
        asignacionRepo.save(a1);
        asignacionRepo.flush();

        // Ahora no debería haber solape para ese empleado/rango
        boolean solapa = asignacionRepo.existeSolapamiento(emp.getId(), i1, f1);
        assertThat(solapa).isFalse();
    }


    @Test
    void asignacionesPorEmpleadoYPorFinca_devuelvenDatosCoherentes() {
        var emp = empleadoRepo.findAll().get(0);
        var finca = fincaRepo.findAll().get(0);
        int antesE = asignacionRepo.findByEmpleadoId(emp.getId()).size();
        int antesF = asignacionRepo.findByFincaId(finca.getId()).size();

        var a = asignacionRepo.save(new Asignacion(LocalDate.now(), null, true, emp, finca));

        assertThat(asignacionRepo.findByEmpleadoId(emp.getId())).hasSize(antesE + 1);
        assertThat(asignacionRepo.findByFincaId(finca.getId())).hasSize(antesF + 1);
    }
}
