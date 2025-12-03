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
public class IniciarSesionEmpleadoTest {

    @Autowired
    private UsuarioService usuarioService;

    @Test
    void empleadoPuedeIniciarSesionConCredencialesValidas() {
        Usuario u = new Usuario();
        u.setUsername("empleadoLogin_test");
        u.setPasswordHash("{noop}claveEmpleado");
        u.setRol(Rol.EMPLEADO);
        u.setActivo(true);

        u = usuarioService.guardar(u);

        Optional<Usuario> loginResult =
                usuarioService.validarLogin("empleadoLogin_test", "claveEmpleado");

        assertTrue(loginResult.isPresent());
        assertEquals(Rol.EMPLEADO, loginResult.get().getRol());
    }

    @Test
    void empleadoInactivoNoPuedeIniciarSesion() {
        Usuario u = new Usuario();
        u.setUsername("empleadoInactivo_test");
        u.setPasswordHash("{noop}claveEmpleado");
        u.setRol(Rol.EMPLEADO);
        u.setActivo(false);

        usuarioService.guardar(u);

        Optional<Usuario> loginResult =
                usuarioService.validarLogin("empleadoInactivo_test", "claveEmpleado");

        assertTrue(loginResult.isEmpty(),
                "Un empleado inactivo no debería poder iniciar sesión");
    }
}
