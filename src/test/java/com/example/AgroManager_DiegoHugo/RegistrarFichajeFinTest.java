package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.*;
import com.example.AgroManager_DiegoHugo.data.repositories.*;
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
class RegistrarFichajeFinTest {

    @Autowired private FichajeRepository fichajeRepo;
    @Autowired private EmpleadoRepository empleadoRepo;
    @Autowired private FincaRepository fincaRepo;

    @Test
    void cerrarFichajeActivo() {
        Empleado emp = empleadoRepo.findAll().get(0);
        Finca finca = fincaRepo.findAll().get(0);

        Fichaje f = fichajeRepo.save(new Fichaje(Instant.now(), null, "INICIO", emp, finca));
        f.setFin(Instant.now());
        f.setEstado("FIN");
        fichajeRepo.save(f);

        Fichaje cerrado = fichajeRepo.findById(f.getId()).orElseThrow();
        assertThat(cerrado.getFin()).isNotNull();
        assertThat(cerrado.getEstado()).isEqualTo("FIN");
    }
}
