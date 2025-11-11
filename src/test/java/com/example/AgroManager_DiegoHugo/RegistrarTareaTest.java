package com.example.AgroManager_DiegoHugo.testsCasosUso;

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
class RegistrarTareaTest {

    @Autowired private TareaRepository tareaRepo;
    @Autowired private EmpleadoRepository empleadoRepo;
    @Autowired private FincaRepository fincaRepo;

    @Test
    void registrarNuevaTareaEnFinca() {
        Empleado emp = empleadoRepo.findAll().get(0);
        Finca finca = fincaRepo.findAll().get(0);

        Tarea tarea = new Tarea(LocalDate.now(), "Poda de árboles", new BigDecimal("4.0"), emp, finca);
        Tarea saved = tareaRepo.save(tarea);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTipo()).contains("Poda");
    }
}
