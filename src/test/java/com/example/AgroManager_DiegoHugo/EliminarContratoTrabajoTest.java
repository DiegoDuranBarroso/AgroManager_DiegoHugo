package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.Contrato;
import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.TipoContrato;
import com.example.AgroManager_DiegoHugo.data.repositories.ContratoRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.EmpleadoRepository;
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
class EliminarContratoTrabajoTest {

    @Autowired private ContratoRepository contratoRepo;
    @Autowired private EmpleadoRepository empleadoRepo;

    @Test
    void eliminarContratoNoEliminaEmpleado() {
        // Empleado existente
        Empleado emp = empleadoRepo.findAll().get(0);

        // Crear contrato de prueba para ese empleado
        Contrato contrato = new Contrato(
                TipoContrato.TEMPORAL,
                LocalDate.now(),
                null,
                new BigDecimal("1200.00"),
                new BigDecimal("10.50"),
                emp
        );
        Contrato guardado = contratoRepo.save(contrato);

        Long contratoId = guardado.getId();
        Long empleadoId = emp.getId();

        assertThat(contratoRepo.findById(contratoId)).isPresent();
        assertThat(empleadoRepo.findById(empleadoId)).isPresent();

        // Acción: eliminar contrato
        contratoRepo.deleteById(contratoId);
        contratoRepo.flush();

        // Comprobaciones: contrato fuera, empleado sigue
        assertThat(contratoRepo.findById(contratoId)).isEmpty();
        assertThat(empleadoRepo.findById(empleadoId)).isPresent();
    }
}
