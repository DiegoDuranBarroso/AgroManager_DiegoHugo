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
class CambiarEstadoFincaTest {

    @Autowired private FincaRepository fincaRepo;

    @Test
    void cambiarEstadoFincaAProduccion() {
        Finca finca = fincaRepo.findAll().get(0);
        finca.setEstado(EstadoFinca.PRODUCCION);
        fincaRepo.save(finca);

        Finca actualizada = fincaRepo.findById(finca.getId()).orElseThrow();
        assertThat(actualizada.getEstado()).isEqualTo(EstadoFinca.PRODUCCION);
    }
}
