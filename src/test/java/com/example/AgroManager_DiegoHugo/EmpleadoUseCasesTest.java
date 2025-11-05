package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.repositories.EmpleadoRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class EmpleadoUseCasesTest {

    @Autowired UsuarioRepository usuarioRepo;
    @Autowired EmpleadoRepository empleadoRepo;

    @Test
    void registrarActualizarEliminarEmpleado() {
        // Registrar usuario + empleado
        Usuario u = new Usuario("emp_test", "hash", Rol.EMPLEADO, true);
        usuarioRepo.save(u);

        Empleado e = new Empleado("99999999Z", "Empleado Test", true, u);
        e = empleadoRepo.save(e);

        assertThat(e.getId()).isNotNull();

        // Actualizar datos
        e.setNombre("Empleado Test Editado");
        empleadoRepo.save(e);

        Empleado rec = empleadoRepo.findById(e.getId()).orElseThrow();
        assertThat(rec.getNombre()).isEqualTo("Empleado Test Editado");

        // Desactivar (equivale a eliminar lógico)
        rec.setActivo(false);
        empleadoRepo.save(rec);
        assertThat(empleadoRepo.findByActivoTrue()).doesNotContain(rec);
    }
}
