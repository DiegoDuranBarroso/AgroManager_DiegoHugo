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
public class EditarFincaTest {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private GerenteRepository gerenteRepository;
    @Autowired private FincaRepository fincaRepository;

    @Test
    void editarFinca_actualizaCamposIncluyendoCoords() {
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
        f.setNombre("Finca A");
        f.setEstado(EstadoFinca.SEMBRADA);
        f.setCiudad("Mérida");
        f.setProvincia("Badajoz");
        f.setArea(10.0);
        f.setLatitud(null);
        f.setLongitud(null);
        f.setGerente(g);
        f = fincaRepository.save(f);

        // Editar finca
        f.setNombre("Finca B");
        f.setEstado(EstadoFinca.LISTA_COSECHA);
        f.setCiudad("Trujillo");
        f.setProvincia("Cáceres");
        f.setArea(15.2);
        f.setLatitud(39.444935);
        f.setLongitud(-5.810764);
        fincaRepository.save(f);

        Finca rec = fincaRepository.findById(f.getId()).orElseThrow();
        assertEquals("Finca B", rec.getNombre());
        assertEquals(EstadoFinca.LISTA_COSECHA, rec.getEstado());
        assertEquals("Trujillo", rec.getCiudad());
        assertEquals("Cáceres", rec.getProvincia());
        assertEquals(15.2, rec.getArea());
        assertEquals(39.444935, rec.getLatitud());
        assertEquals(-5.810764, rec.getLongitud());
    }
}
