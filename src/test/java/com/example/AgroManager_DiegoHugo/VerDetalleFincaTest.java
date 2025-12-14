package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.*;
import com.example.AgroManager_DiegoHugo.data.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class VerDetalleFincaTest {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private GerenteRepository gerenteRepository;
    @Autowired private FincaRepository fincaRepository;

    @Test
    void verDetalleFinca_recuperaFincaConDatos() {
        Usuario ug = new Usuario();
        ug.setUsername("ger_" + UUID.randomUUID());
        ug.setPasswordHash("hash");
        ug.setRol(Rol.GERENTE);
        ug.setActivo(true);
        ug = usuarioRepository.save(ug);

        Gerente g = new Gerente();
        g.setNombre("Gerente");
        g.setEmail("g@mail.com");
        g.setTelefono("600000000");
        g.setUsuario(ug);
        g = gerenteRepository.save(g);

        Finca f = new Finca();
        f.setNombre("Finca Detalle");
        f.setEstado(EstadoFinca.MANTENIMIENTO);
        f.setCiudad("Mérida");
        f.setProvincia("Badajoz");
        f.setArea(12.4);
        f.setLatitud(38.902584);
        f.setLongitud(-6.307457);
        f.setGerente(g);
        f = fincaRepository.save(f);

        // Caso de uso: "ver detalle"
        Finca rec = fincaRepository.findById(f.getId()).orElseThrow();

        assertEquals("Finca Detalle", rec.getNombre());
        assertNotNull(rec.getGerente());
        assertEquals(g.getId(), rec.getGerente().getId());
        assertNotNull(rec.getEstado());
        assertNotNull(rec.getLatitud());
        assertNotNull(rec.getLongitud());
    }
}
