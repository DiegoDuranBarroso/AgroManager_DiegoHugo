package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
import com.example.AgroManager_DiegoHugo.data.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class IniciarSesionGerenteTest {

    @Autowired
    private UsuarioService usuarioService;

    @Test
    void gerentePuedeIniciarSesionConCredencialesValidas() {
        Usuario u = new Usuario();
        u.setUsername("gerenteLogin_test");
        u.setPasswordHash("{noop}claveSegura");
        u.setRol(Rol.GERENTE);
        u.setActivo(true);

        u = usuarioService.guardar(u);

        Optional<Usuario> loginResult =
                usuarioService.validarLogin("gerenteLogin_test", "claveSegura");

        assertTrue(loginResult.isPresent(), "El login debería ser correcto");
        assertEquals(u.getId(), loginResult.get().getId());
        assertEquals(Rol.GERENTE, loginResult.get().getRol());
    }

    @Test
    void gerenteNoPuedeIniciarSesionConPasswordIncorrecta() {
        Usuario u = new Usuario();
        u.setUsername("gerenteLogin_fail");
        u.setPasswordHash("{noop}claveCorrecta");
        u.setRol(Rol.GERENTE);
        u.setActivo(true);

        usuarioService.guardar(u);

        Optional<Usuario> loginResult =
                usuarioService.validarLogin("gerenteLogin_fail", "otraCosa");

        assertTrue(loginResult.isEmpty(),
                "No debería loguearse con password incorrecta");
    }
}
