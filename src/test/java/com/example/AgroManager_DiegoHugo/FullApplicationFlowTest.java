package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.*;
import com.example.AgroManager_DiegoHugo.data.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class FullApplicationFlowTest {

    @Autowired
    UsuarioRepository usuarioRepo;
    @Autowired
    EmpleadoRepository empleadoRepo;
    @Autowired FincaRepository fincaRepo;
    @Autowired
    AsignacionRepository asignacionRepo;
    @Autowired
    FichajeRepository fichajeRepo;
    @Autowired TareaRepository tareaRepo;
    @Autowired ContratoRepository contratoRepo;
    @Autowired
    NominaRepository nominaRepo;

    @Test
    void flujoCompleto() {
        // 1) Crear empleado + usuario
        Usuario u = new Usuario("flow_user", "hash", Rol.EMPLEADO, true);
        usuarioRepo.save(u);
        Empleado e = new Empleado("00000000X", "Flow Tester", true, u);
        empleadoRepo.save(e);

        // 2) Elegir finca existente y asignar (sin solapes)
        Finca f = fincaRepo.findAll().get(0);
        LocalDate hoy = LocalDate.now();
        boolean solapa = asignacionRepo.existeSolapamiento(e.getId(), hoy, hoy.plusDays(10));
        assertThat(solapa).isFalse();
        asignacionRepo.save(new Asignacion(hoy, hoy.plusDays(10), true, e, f));

        // 3) Fichaje inicio -> fin
        var activoPrevio = fichajeRepo.findFirstByEmpleadoIdAndFinIsNullOrderByInicioDesc(e.getId());
        activoPrevio.ifPresent(a -> { a.setFin(Instant.now()); fichajeRepo.save(a); });

        Fichaje in = new Fichaje(Instant.now(), null, "ABIERTO", e, f);
        fichajeRepo.save(in);
        in.setFin(Instant.now().plusSeconds(3600));
        in.setEstado("CERRADO");
        fichajeRepo.save(in);

        // 4) Registrar tarea
        tareaRepo.save(new Tarea(hoy, "Trabajo campo", new BigDecimal("2.5"), e, f));

        // 5) Crear contrato y generar nómina simple
        Contrato c = new Contrato(
                TipoContrato.TEMPORAL,
                hoy.minusDays(5), null,
                new BigDecimal("1000.00"),
                new BigDecimal("10.00"),
                e
        );
        contratoRepo.save(c);

        BigDecimal horas = tareaRepo.sumHoras(e.getId(), hoy.withDayOfMonth(1), hoy.withDayOfMonth(1).plusMonths(1).minusDays(1));
        if (horas == null) horas = BigDecimal.ZERO;

        BigDecimal total = c.getSalarioBase().add(c.getTarifaHora().multiply(horas));
        Nomina n = new Nomina(hoy.withDayOfMonth(1), hoy.withDayOfMonth(1).plusMonths(1).minusDays(1), total, "GENERADA", e);
        nominaRepo.save(n);

        // 6) Asserts finales
        assertThat(usuarioRepo.findByUsername("flow_user")).isPresent();
        assertThat(empleadoRepo.findByDni("00000000X")).isPresent();
        assertThat(tareaRepo.findByEmpleadoId(e.getId())).isNotEmpty();
        assertThat(nominaRepo.findByEmpleadoIdOrderByPeriodoInicioDesc(e.getId())).isNotEmpty();
    }
}
