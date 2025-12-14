package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
import com.example.AgroManager_DiegoHugo.data.repositories.EmpleadoRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class EditarPerfilEmpleadoTest {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EmpleadoRepository empleadoRepository;

    @Test
    void editarPerfilEmpleado_actualizaNombreYDniYFotoPerfil() {

        // 1) Crear usuario (EMPLEADO)
        Usuario u = new Usuario();
        u.setUsername("emp_" + UUID.randomUUID());
        u.setPasswordHash("hash");
        u.setRol(Rol.EMPLEADO);
        u.setActivo(true);
        u.setFotoPerfil(null);
        u = usuarioRepository.save(u);

        // 2) Crear empleado asociado (OJO: optional=false, debe tener usuario)
        Empleado e = new Empleado();
        e.setDni("12345678Z");
        e.setNombre("Pepe");
        e.setActivo(true);
        e.setUsuario(u);
        e = empleadoRepository.save(e);

        // 3) Editar “perfil”
        e.setNombre("Pepe Pérez");
        e.setDni("87654321X");
        e.getUsuario().setFotoPerfil("pepe.jpg");

        empleadoRepository.save(e);

        // 4) Comprobaciones
        Empleado rec = empleadoRepository.findById(e.getId()).orElseThrow();
        assertEquals("Pepe Pérez", rec.getNombre());
        assertEquals("87654321X", rec.getDni());
        assertTrue(rec.getActivo());

        Usuario uRec = usuarioRepository.findById(u.getId()).orElseThrow();
        assertEquals("pepe.jpg", uRec.getFotoPerfil());
        assertEquals(Rol.EMPLEADO, uRec.getRol());
    }
}
