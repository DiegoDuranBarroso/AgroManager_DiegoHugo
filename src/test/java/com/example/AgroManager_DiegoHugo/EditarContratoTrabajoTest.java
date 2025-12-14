package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.*;
import com.example.AgroManager_DiegoHugo.data.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class EditarContratoTrabajoTest {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private ContratoRepository contratoRepository;

    @Test
    void editarContrato_actualizaFechasYSueldos() {
        Usuario ue = new Usuario();
        ue.setUsername("emp_" + UUID.randomUUID());
        ue.setPasswordHash("hash");
        ue.setRol(Rol.EMPLEADO);
        ue.setActivo(true);
        ue = usuarioRepository.save(ue);

        Empleado e = new Empleado();
        e.setDni("12345678Z");
        e.setNombre("Empleado");
        e.setActivo(true);
        e.setUsuario(ue);
        e = empleadoRepository.save(e);

        Contrato c = new Contrato();
        c.setTipo(TipoContrato.TEMPORAL);
        c.setFechaInicio(LocalDate.now());
        c.setFechaFin(LocalDate.now().plusMonths(2));
        c.setSalarioBase(new BigDecimal("1200.00"));
        c.setTarifaHora(new BigDecimal("10.00"));
        c.setEmpleado(e);
        c = contratoRepository.save(c);

        // Editar contrato
        c.setTipo(TipoContrato.INDEFINIDO);
        c.setFechaFin(null);
        c.setSalarioBase(new BigDecimal("1500.00"));
        c.setTarifaHora(new BigDecimal("12.50"));
        contratoRepository.save(c);

        Contrato rec = contratoRepository.findById(c.getId()).orElseThrow();
        assertEquals(TipoContrato.INDEFINIDO, rec.getTipo());
        assertNull(rec.getFechaFin());
        assertEquals(new BigDecimal("1500.00"), rec.getSalarioBase());
        assertEquals(new BigDecimal("12.50"), rec.getTarifaHora());
    }
}
