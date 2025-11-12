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
class CrearContratoTest {

    @Autowired private ContratoRepository contratoRepo;
    @Autowired private EmpleadoRepository empleadoRepo;

    @Test
    void crearContratoIndefinidoParaEmpleado() {
        Empleado emp = empleadoRepo.findAll().get(0);
        Contrato contrato = new Contrato(
                TipoContrato.INDEFINIDO,
                LocalDate.now(),
                null,
                new BigDecimal("1300.00"),
                new BigDecimal("12.50"),
                emp
        );

        Contrato saved = contratoRepo.save(contrato);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTipo()).isEqualTo(TipoContrato.INDEFINIDO);
    }
}
