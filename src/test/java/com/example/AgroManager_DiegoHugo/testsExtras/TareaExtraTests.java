package com.example.AgroManager_DiegoHugo.testsExtras;

import com.example.AgroManager_DiegoHugo.data.repositories.*;
import com.example.AgroManager_DiegoHugo.data.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class TareaExtraTests {

    @Autowired TareaRepository tareaRepo;
    @Autowired EmpleadoRepository empleadoRepo;
    @Autowired FincaRepository fincaRepo;

    @Test
    void sumHoras_sinTareasDevuelveCero() {
        var emp = empleadoRepo.findAll().get(0);
        LocalDate hoy = LocalDate.now();
        var horas = tareaRepo.sumHoras(emp.getId(), hoy.plusYears(10), hoy.plusYears(10).plusDays(1));
        assertThat(horas).isNotNull();
        assertThat(horas).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void filtrosPorEmpleadoYFinca_funcionan() {
        var emp = empleadoRepo.findAll().get(0);
        var finca = fincaRepo.findAll().get(0);
        int antesE = tareaRepo.findByEmpleadoId(emp.getId()).size();
        int antesF = tareaRepo.findByFincaId(finca.getId()).size();

        tareaRepo.save(new Tarea(LocalDate.now(), "Labores", new BigDecimal("1.0"), emp, finca));

        assertThat(tareaRepo.findByEmpleadoId(emp.getId())).hasSize(antesE + 1);
        assertThat(tareaRepo.findByFincaId(finca.getId())).hasSize(antesF + 1);
    }

    @Test
    void sumHoras_incluyeBordesDelRango() {
        var emp = empleadoRepo.findAll().get(0);
        var finca = fincaRepo.findAll().get(0);
        LocalDate d = LocalDate.now();

        tareaRepo.save(new Tarea(d, "Borde", new BigDecimal("2.0"), emp, finca));

        var horas = tareaRepo.sumHoras(emp.getId(), d, d);
        assertThat(horas).isEqualByComparingTo("2.0");
    }
}
