package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.model.Tarea;
import com.example.AgroManager_DiegoHugo.data.repositories.EmpleadoRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.FincaRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.TareaRepository;
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
class TareaUseCaseTest {

    @Autowired TareaRepository tareaRepo;
    @Autowired EmpleadoRepository empleadoRepo;
    @Autowired FincaRepository fincaRepo;

    @Test
    void registrarTareaYSumarHoras() {
        Empleado emp = empleadoRepo.findAll().get(0);
        Finca finca = fincaRepo.findAll().get(0);

        Tarea t = new Tarea(LocalDate.now(), "Prueba", new BigDecimal("3.5"), emp, finca);
        tareaRepo.save(t);

        var total = tareaRepo.sumHoras(emp.getId(), LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        assertThat(total).isNotNull();
        assertThat(total).isGreaterThan(BigDecimal.ZERO);
    }
}
