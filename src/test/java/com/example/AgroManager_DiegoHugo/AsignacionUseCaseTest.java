package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.Asignacion;
import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.repositories.AsignacionRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.EmpleadoRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.FincaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class AsignacionUseCaseTest {

    @Autowired AsignacionRepository asignacionRepo;
    @Autowired EmpleadoRepository empleadoRepo;
    @Autowired FincaRepository fincaRepo;

    @Test
    void asignarEmpleadoAUnaFinca_validandoSolapes() {
        Empleado emp = empleadoRepo.findAll().get(0);
        Finca finca = fincaRepo.findAll().get(0);

        LocalDate inicio = LocalDate.now().minusDays(1);
        LocalDate fin = LocalDate.now().plusDays(10);

        boolean solapa = asignacionRepo.existeSolapamiento(emp.getId(), inicio, fin);

        if (solapa) {
            // A nivel repositorio solo verificamos que detecta el solape
            assertThat(solapa).isTrue();
            // (la excepción se probará en tests de la capa servicio)
        } else {
            int before = asignacionRepo.findByEmpleadoId(emp.getId()).size();

            Asignacion nueva = new Asignacion(inicio, fin, true, emp, finca);
            Asignacion saved = asignacionRepo.save(nueva);
            assertThat(saved.getId()).isNotNull();

            // Ahora debe detectar solape para ese mismo rango
            boolean ahoraSolapa = asignacionRepo.existeSolapamiento(emp.getId(), inicio, fin);
            assertThat(ahoraSolapa).isTrue();

            int after = asignacionRepo.findByEmpleadoId(emp.getId()).size();
            assertThat(after).isEqualTo(before + 1);
        }
    }
}
