package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.*;
import com.example.AgroManager_DiegoHugo.data.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ActualizarEmpleadoTest {

    @Autowired private EmpleadoRepository empleadoRepo;

    @Test
    void actualizarNombreYEstadoEmpleado() {
        Empleado emp = empleadoRepo.findAll().get(0);
        String nuevoNombre = emp.getNombre() + "_edit";
        emp.setNombre(nuevoNombre);
        empleadoRepo.save(emp);

        Empleado actualizado = empleadoRepo.findById(emp.getId()).orElseThrow();
        assertThat(actualizado.getNombre()).isEqualTo(nuevoNombre);
    }
}
