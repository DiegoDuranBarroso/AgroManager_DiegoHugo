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
class ContratoUseCaseTest {

    @Autowired ContratoRepository contratoRepo;
    @Autowired EmpleadoRepository empleadoRepo;

    @Test
    void crearYComprobarContratoVigente() {
        Empleado emp = empleadoRepo.findAll().get(0);

        Contrato c = new Contrato(
                TipoContrato.INDEFINIDO,
                LocalDate.now().minusMonths(1),
                null,
                new BigDecimal("1200.00"),
                new BigDecimal("12.50"),
                emp
        );
        contratoRepo.save(c);

        var vigentes = contratoRepo.findVigentesEn(emp.getId(), LocalDate.now());
        assertThat(vigentes).isNotEmpty();
    }

}
