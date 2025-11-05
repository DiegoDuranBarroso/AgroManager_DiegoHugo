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
class NominaExtraTests {

    @Autowired NominaRepository nominaRepo;
    @Autowired EmpleadoRepository empleadoRepo;

    @Test
    void findEnFecha_encuentraNominaDelPeriodo() {
        var emp = empleadoRepo.findAll().get(0);

        LocalDate i = LocalDate.now().withDayOfMonth(1);
        LocalDate f = i.plusMonths(1).minusDays(1);

        var n = nominaRepo.save(new Nomina(i, f, new BigDecimal("1234.56"), "GENERADA", emp));

        var lista = nominaRepo.findEnFecha(emp.getId(), i.plusDays(2));
        assertThat(lista).isNotEmpty();
        assertThat(lista.get(0).getId()).isEqualTo(n.getId());
    }

    @Test
    void findByEstado_devuelveSoloEseEstado() {
        var emp = empleadoRepo.findAll().get(0);
        LocalDate i = LocalDate.now().minusMonths(2).withDayOfMonth(1);
        LocalDate f = i.plusMonths(1).minusDays(1);

        nominaRepo.save(new Nomina(i, f, new BigDecimal("1000"), "PAGADA", emp));
        var pagadas = nominaRepo.findByEstado("PAGADA");
        assertThat(pagadas.stream().allMatch(n -> "PAGADA".equals(n.getEstado()))).isTrue();
    }

    @Test
    void evitarDuplicadoMismoPeriodoMismoEmpleado_aNivelTest() {
        var emp = empleadoRepo.findAll().get(0);
        LocalDate i = LocalDate.of(2025,1,1);
        LocalDate f = LocalDate.of(2025,1,31);

        var ya = nominaRepo.findEnFecha(emp.getId(), i.plusDays(5));
        int antes = ya.size();

        nominaRepo.save(new Nomina(i, f, new BigDecimal("999"), "GENERADA", emp));

        var despues = nominaRepo.findEnFecha(emp.getId(), i.plusDays(5));
        assertThat(despues.size()).isEqualTo(antes + 1);
    }
}
