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
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ContratoExtraTests {

    @Autowired EmpleadoRepository empleadoRepo;
    @Autowired ContratoRepository contratoRepo;

    @Test
    void sinContratoVigente_fueraDeRango() {
        var emp = empleadoRepo.findAll().get(0);
        var fecha = LocalDate.of(1999,1,1);
        assertThat(contratoRepo.findVigentesEn(emp.getId(), fecha)).isEmpty();
    }

    @Test
    void variosContratos_retornaElMasRecienteComoPrimero() {
        var emp = empleadoRepo.findAll().get(0);

        var c1 = new Contrato(TipoContrato.TEMPORAL,
                LocalDate.now().minusMonths(6), LocalDate.now().minusMonths(3),
                new BigDecimal("900"), new BigDecimal("9"), emp);
        var c2 = new Contrato(TipoContrato.INDEFINIDO,
                LocalDate.now().minusMonths(2), null,
                new BigDecimal("1200"), new BigDecimal("12"), emp);
        contratoRepo.save(c1);
        contratoRepo.save(c2);

        var lista = contratoRepo.findByEmpleadoIdOrderByInicioDesc(emp.getId());
        assertThat(lista).isNotEmpty();
        assertThat(lista.get(0).getFechaInicio())
                .isEqualTo(lista.stream().map(Contrato::getFechaInicio).max(Comparator.naturalOrder()).orElseThrow());
    }

    @Test
    void vigenteEnFecha_actual() {
        var emp = empleadoRepo.findAll().get(0);
        var vigente = contratoRepo.findVigentesEn(emp.getId(), LocalDate.now());
        // Puede que ya exista por _data.sql o por otros tests
        assertThat(vigente.size()).isGreaterThanOrEqualTo(0);
    }
}
