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

    @Autowired
    EmpleadoRepository empleadoRepo;
    @Autowired
    UsuarioRepository usuarioRepo;
    @Autowired
    TareaRepository tareaRepo;
    @Autowired
    FichajeRepository fichajeRepo;
    @Autowired
    AsignacionRepository asignacionRepo;
    @Autowired
    ContratoRepository contratoRepo;

    @Test
    void eliminarEmpleadoExistente() {
        Empleado emp = empleadoRepo.findAll().get(1);
        Long empId = emp.getId();
        Long userId = emp.getUsuario().getId();

        empleadoRepo.deleteById(empId);
        empleadoRepo.flush();

        assertThat(empleadoRepo.findById(empId)).isEmpty();
        assertThat(usuarioRepo.findById(userId)).isEmpty();
        assertThat(tareaRepo.findByEmpleadoId(empId)).isEmpty();
        assertThat(fichajeRepo.findByEmpleadoId(empId)).isEmpty();
        assertThat(asignacionRepo.findByEmpleadoId(empId)).isEmpty();
        assertThat(contratoRepo.findByEmpleadoId(empId, org.springframework.data.domain.Sort.unsorted())).isEmpty();
    }
}
