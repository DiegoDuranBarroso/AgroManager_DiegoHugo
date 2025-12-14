package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.Gerente;
import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
import com.example.AgroManager_DiegoHugo.data.repositories.GerenteRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class EditarPerfilGerenteTest {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private GerenteRepository gerenteRepository;

    @Test
    void editarPerfilGerente_actualizaDatosPersonalesYFotoPerfil() {

        // 1) Crear usuario (GERENTE)
        Usuario u = new Usuario();
        u.setUsername("ger_" + UUID.randomUUID());
        u.setPasswordHash("hash");
        u.setRol(Rol.GERENTE);
        u.setActivo(true);
        u.setFotoPerfil(null);
        u = usuarioRepository.save(u);

        // 2) Crear gerente asociado
        Gerente g = new Gerente();
        g.setNombre("Carlos");
        g.setEmail("old@mail.com");
        g.setTelefono("600000000");
        g.setUsuario(u);
        g = gerenteRepository.save(g);

        // 3) Editar “perfil”
        g.setNombre("Carlos García");
        g.setEmail("nuevo@mail.com");
        g.setTelefono("611111111");

        // Foto de perfil está en Usuario, no en Gerente
        g.getUsuario().setFotoPerfil("carlos.png");

        gerenteRepository.save(g);

        // 4) Comprobaciones
        Gerente rec = gerenteRepository.findById(g.getId()).orElseThrow();
        assertEquals("Carlos García", rec.getNombre());
        assertEquals("nuevo@mail.com", rec.getEmail());
        assertEquals("611111111", rec.getTelefono());

        Usuario uRec = usuarioRepository.findById(u.getId()).orElseThrow();
        assertEquals("carlos.png", uRec.getFotoPerfil());
        assertEquals(Rol.GERENTE, uRec.getRol());
        assertTrue(uRec.isActivo());
    }
}
