package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Fichaje;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.repositories.EmpleadoRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.FichajeRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.FincaRepository;
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
class FichajeUseCasesTest {

    @Autowired FichajeRepository fichajeRepo;
    @Autowired EmpleadoRepository empleadoRepo;
    @Autowired FincaRepository fincaRepo;

    @Test
    void registrarInicioYFinFichaje() {
        Empleado emp = empleadoRepo.findAll().get(0);
        Finca finca = fincaRepo.findAll().get(0);

        // Cerrar activo si existiera (simulación de política)
        fichajeRepo.findFirstByEmpleadoIdAndFinIsNullOrderByInicioDesc(emp.getId())
                .ifPresent(f -> { f.setFin(Instant.now()); fichajeRepo.save(f); });

        // Inicio
        Fichaje inicio = new Fichaje(Instant.now(), null, "ABIERTO", emp, finca);
        inicio = fichajeRepo.save(inicio);
        assertThat(inicio.getId()).isNotNull();

        // Fin
        inicio.setFin(Instant.now().plusSeconds(3600));
        inicio.setEstado("CERRADO");
        fichajeRepo.save(inicio);

        var activo = fichajeRepo.findFirstByEmpleadoIdAndFinIsNullOrderByInicioDesc(emp.getId());
        assertThat(activo).isEmpty();
    }
}
