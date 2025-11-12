package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.*;
import com.example.AgroManager_DiegoHugo.data.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class RegistrarEmpleadoTest {

    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private EmpleadoRepository empleadoRepo;

    @Test
    void registrarEmpleado_conUsuarioActivo() {
        Usuario usuario = usuarioRepo.save(new Usuario("emp_" + UUID.randomUUID(), "hashedpwd", Rol.EMPLEADO, true));
        Empleado empleado = new Empleado("98765432Z", "Juan Pérez", true, usuario);
        Usuario usuario1 = usuarioRepo.save(new Usuario("lolo" + UUID.randomUUID(), "hashedpwd", Rol.EMPLEADO, true));
        Empleado empleado1 = new Empleado("12345678A", "DIEGO DURAN", true, usuario1);

        Empleado saved = empleadoRepo.save(empleado);
        Empleado savedd = empleadoRepo.save(empleado1);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsuario().isActivo()).isTrue();
        assertThat(saved.getUsuario().getRol()).isEqualTo(Rol.EMPLEADO);
    }
}
