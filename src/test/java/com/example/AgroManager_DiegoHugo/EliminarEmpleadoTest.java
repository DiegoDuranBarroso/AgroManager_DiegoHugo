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
class EliminarEmpleadoTest {

    @Autowired private EmpleadoRepository empleadoRepo;

    @Test
    void eliminarEmpleadoExistente() {
        Empleado emp = empleadoRepo.findAll().get(0);
        Long id = emp.getId();

        empleadoRepo.deleteById(id);
        empleadoRepo.flush();

        assertThat(empleadoRepo.findById(id)).isEmpty();
    }
}
