package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.*;
import com.example.AgroManager_DiegoHugo.data.repositories.*;
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
class GenerarNominaMensualTest {

    @Autowired private NominaRepository nominaRepo;
    @Autowired private EmpleadoRepository empleadoRepo;

    @Test
    void generarNominaDeEmpleadoPorMes() {
        Empleado emp = empleadoRepo.findAll().get(0);
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = inicio.plusMonths(1).minusDays(1);

        Nomina n = new Nomina(inicio, fin, new BigDecimal("1450.00"), "GENERADA", emp);
        Nomina saved = nominaRepo.save(n);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmpleado().getId()).isEqualTo(emp.getId());
        assertThat(saved.getEstado()).isEqualTo("GENERADA");
    }
}
