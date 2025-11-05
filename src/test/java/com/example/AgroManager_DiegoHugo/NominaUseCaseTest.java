package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Nomina;
import com.example.AgroManager_DiegoHugo.data.repositories.ContratoRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.EmpleadoRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.NominaRepository;
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
class NominaUseCaseTest {

    @Autowired
    NominaRepository nominaRepo;
    @Autowired
    ContratoRepository contratoRepo;
    @Autowired
    EmpleadoRepository empleadoRepo;
    @Autowired
    TareaRepository tareaRepo;

    @Test
    void generarNominaMensual_basica() {
        Empleado emp = empleadoRepo.findAll().get(0);

        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = inicioMes.plusMonths(1).minusDays(1);

        // contrato vigente
        var contrato = contratoRepo.findVigentesEn(emp.getId(), inicioMes.plusDays(10))
                .stream().findFirst().orElse(null);
        assertThat(contrato).as("Debe existir contrato vigente").isNotNull();

        // horas trabajadas
        BigDecimal horas = tareaRepo.sumHoras(emp.getId(), inicioMes, finMes);
        if (horas == null) horas = BigDecimal.ZERO;

        BigDecimal total = contrato.getSalarioBase().add(contrato.getTarifaHora().multiply(horas));

        Nomina n = new Nomina(inicioMes, finMes, total, "GENERADA", emp);
        n = nominaRepo.save(n);

        assertThat(n.getId()).isNotNull();
        assertThat(n.getTotalBruto()).isEqualByComparingTo(total);
    }
}
