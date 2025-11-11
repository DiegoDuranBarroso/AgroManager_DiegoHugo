package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.*;
import com.example.AgroManager_DiegoHugo.data.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class AsignarEmpleadoAFincaTest {

    @Autowired private AsignacionRepository asignacionRepo;
    @Autowired private EmpleadoRepository empleadoRepo;
    @Autowired private FincaRepository fincaRepo;

    @Test
    void asignarEmpleadoAFinca_validaPersistencia() {
        Empleado emp = empleadoRepo.findAll().get(0);
        Finca finca = fincaRepo.findAll().get(0);

        Asignacion nueva = new Asignacion(LocalDate.now(), null, true, emp, finca);
        Asignacion saved = asignacionRepo.save(nueva);

        assertThat(saved.getId()).isNotNull();

        List<Asignacion> asignaciones = asignacionRepo.findByEmpleadoId(emp.getId());
        assertThat(asignaciones).extracting("finca.id").contains(finca.getId());
    }
}
