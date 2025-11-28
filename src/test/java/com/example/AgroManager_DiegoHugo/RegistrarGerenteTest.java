package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.Gerente;
import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
import com.example.AgroManager_DiegoHugo.data.repositories.GerenteRepository;
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
class RegistrarGerenteTest {

    @Autowired private GerenteRepository gerenteRepo;
    @Autowired private UsuarioRepository usuarioRepo;

    @Test
    void registrarNuevoGerenteConUsuarioGerente() {
        long gerentesAntes = gerenteRepo.count();

        // 1) Crear usuario con rol GERENTE
        Usuario usuario = new Usuario();
        usuario.setUsername("gerente_test");
        usuario.setPasswordHash("{noop}test123"); // sin codificar, solo para pruebas
        usuario.setRol(Rol.GERENTE);
        usuario.setActivo(true);

        Usuario usuarioGuardado = usuarioRepo.save(usuario);

        // 2) Crear gerente asociado a ese usuario
        Gerente gerente = new Gerente();
        gerente.setNombre("Gerente Test");
        gerente.setEmail("gerente.test@agro.com");
        gerente.setTelefono("600000000");
        gerente.setUsuario(usuarioGuardado);

        Gerente guardado = gerenteRepo.save(gerente);

        // 3) Comprobaciones
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getUsuario()).isNotNull();
        assertThat(guardado.getUsuario().getRol()).isEqualTo(Rol.GERENTE);
        assertThat(gerenteRepo.count()).isEqualTo(gerentesAntes + 1);
    }
}
