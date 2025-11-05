package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.EstadoFinca;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.repositories.FincaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class FincaUseCaseTest {

    @Autowired FincaRepository fincaRepo;

    @Test
    void cambiarEstadoDeFinca() {
        Finca f = fincaRepo.findAll().get(0);
        f.setEstado(EstadoFinca.MANTENIMIENTO);
        fincaRepo.save(f);

        Finca rec = fincaRepo.findById(f.getId()).orElseThrow();
        assertThat(rec.getEstado()).isEqualTo(EstadoFinca.MANTENIMIENTO);
    }
}
