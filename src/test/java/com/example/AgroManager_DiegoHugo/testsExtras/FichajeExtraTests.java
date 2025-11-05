package com.example.AgroManager_DiegoHugo.testsExtras;

import com.example.AgroManager_DiegoHugo.data.repositories.*;
import com.example.AgroManager_DiegoHugo.data.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class FichajeExtraTests {

    @Autowired FichajeRepository fichajeRepo;
    @Autowired EmpleadoRepository empleadoRepo;
    @Autowired FincaRepository fincaRepo;

    @Test
    void findActivo_retornaElMasRecienteSinFin() {
        Empleado emp = empleadoRepo.findAll().get(0);
        Finca finca = fincaRepo.findAll().get(0);

        // cerrar si hay uno
        fichajeRepo.findFirstByEmpleadoIdAndFinIsNullOrderByInicioDesc(emp.getId())
                .ifPresent(f -> { f.setFin(Instant.now()); fichajeRepo.save(f); });

        // crear dos abiertos, el segundo debe ser el activo retornado
        var f1 = fichajeRepo.save(new Fichaje(Instant.now().minusSeconds(3600), null, "ABIERTO", emp, finca));
        var f2 = fichajeRepo.save(new Fichaje(Instant.now(), null, "ABIERTO", emp, finca));

        var activo = fichajeRepo.findFirstByEmpleadoIdAndFinIsNullOrderByInicioDesc(emp.getId());
        assertThat(activo).isPresent();
        assertThat(activo.get().getId()).isEqualTo(f2.getId());
    }

    @Test
    void findByInicioBetween_incluyeBordes() {
        Empleado emp = empleadoRepo.findAll().get(0);
        Finca finca = fincaRepo.findAll().get(0);

        Instant t0 = Instant.now().minusSeconds(7200);
        Instant t1 = Instant.now().minusSeconds(3600);
        fichajeRepo.save(new Fichaje(t0, t0.plusSeconds(1200), "CERRADO", emp, finca));
        fichajeRepo.save(new Fichaje(t1, t1.plusSeconds(1200), "CERRADO", emp, finca));

        var lista = fichajeRepo.findByInicioBetween(t0, t1);
        assertThat(lista).isNotEmpty();
    }
}
