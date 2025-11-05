package com.example.AgroManager_DiegoHugo.testsExtras;

import com.example.AgroManager_DiegoHugo.data.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class BasicosExtraTests {

    @Autowired EmpleadoRepository empleadoRepo;
    @Autowired UsuarioRepository usuarioRepo;
    @Autowired FincaRepository fincaRepo;

    @Test
    void empleadosActivos_excluyeInactivos() {
        var e = empleadoRepo.findAll().get(0);
        e.setActivo(false);
        empleadoRepo.save(e);

        assertThat(empleadoRepo.findByActivoTrue().stream().anyMatch(x -> x.getId().equals(e.getId()))).isFalse();
    }

    @Test
    void buscarEmpleadoPorNombre_ignoreCase() {
        var lista = empleadoRepo.findByNombreContainingIgnoreCase("die");
        assertThat(lista).isNotNull();
    }

    @Test
    void buscarFincaPorNombre_ignoreCase() {
        var lista = fincaRepo.findByNombreContainingIgnoreCase("finca");
        assertThat(lista).isNotEmpty();
    }

    @Test
    void buscarUsuarioPorUsername() {
        var u = usuarioRepo.findByUsername("gerente1");
        assertThat(u).isPresent();
    }
}
