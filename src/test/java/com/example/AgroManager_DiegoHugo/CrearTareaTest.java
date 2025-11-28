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
class CrearTareaTest {

    @Autowired private TareaRepository tareaRepo;
    @Autowired private EmpleadoRepository empleadoRepo;
    @Autowired private FincaRepository fincaRepo;

    @Test
    void crearNuevaTareaParaEmpleadoYFinca() {
        Empleado emp = empleadoRepo.findAll().get(0);
        Finca finca = fincaRepo.findAll().get(0);

        long tareasAntes = tareaRepo.count();

        Tarea tarea = new Tarea();
        tarea.setFecha(LocalDate.now());
        tarea.setTipo("PODA_TEST");
        tarea.setHoras(new BigDecimal("2.50"));
        tarea.setEmpleado(emp);
        tarea.setFinca(finca);

        Tarea guardada = tareaRepo.save(tarea);

        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getEmpleado().getId()).isEqualTo(emp.getId());
        assertThat(guardada.getFinca().getId()).isEqualTo(finca.getId());
        assertThat(tareaRepo.count()).isEqualTo(tareasAntes + 1);
    }
}
