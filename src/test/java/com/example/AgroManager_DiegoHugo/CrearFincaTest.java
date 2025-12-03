package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.*;
import com.example.AgroManager_DiegoHugo.data.repositories.FincaRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.GerenteRepository;
import com.example.AgroManager_DiegoHugo.data.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CrearFincaTest {

    @Autowired
    private FincaRepository fincaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private GerenteRepository gerenteRepository;

    private Gerente crearGerenteTest(String sufijo) {
        Usuario u = new Usuario();
        u.setUsername("gerenteCrearFinca_" + sufijo);
        u.setPasswordHash("{noop}pass");
        u.setRol(Rol.GERENTE);
        u.setActivo(true);
        u = usuarioService.guardar(u);

        Gerente g = new Gerente();
        g.setNombre("Gerente Fincas " + sufijo);
        g.setEmail("g" + sufijo + "@test.com");
        g.setUsuario(u);
        return gerenteRepository.save(g);
    }

    @Test
    void gerentePuedeCrearUnaNuevaFinca() {
        Gerente gerente = crearGerenteTest("crear");

        Finca finca = new Finca();
        finca.setNombre("Finca Test Crear");
        finca.setEstado(EstadoFinca.SEMBRADA);
        finca.setCiudad("Ciudad Finca");
        finca.setProvincia("Provincia Finca");
        finca.setArea(15.5);
        finca.setGerente(gerente);

        Finca guardada = fincaRepository.save(finca);

        assertNotNull(guardada.getId());
        assertEquals("Finca Test Crear", guardada.getNombre());
        assertEquals(EstadoFinca.SEMBRADA, guardada.getEstado());
        assertEquals(gerente.getId(), guardada.getGerente().getId());
    }
}
