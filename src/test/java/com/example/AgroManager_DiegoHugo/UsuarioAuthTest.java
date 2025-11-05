package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.Usuario;
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
class UsuarioAuthTest {

    @Autowired UsuarioRepository usuarioRepo;

    @Test
    void buscarUsuarioPorUsername() {
        Usuario u = usuarioRepo.findByUsername("gerente1").orElse(null);
        assertThat(u).isNotNull();
        assertThat(u.isActivo()).isTrue();
    }
}
