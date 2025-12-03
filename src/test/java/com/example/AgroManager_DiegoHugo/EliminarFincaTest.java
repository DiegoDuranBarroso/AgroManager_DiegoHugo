package com.example.AgroManager_DiegoHugo;

import com.example.AgroManager_DiegoHugo.data.model.EstadoFinca;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.model.Gerente;
import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
import com.example.AgroManager_DiegoHugo.data.repositories.GerenteRepository;
import com.example.AgroManager_DiegoHugo.data.services.FincaService;
import com.example.AgroManager_DiegoHugo.data.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class EliminarFincaTest {

    @Autowired
    private FincaService fincaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private GerenteRepository gerenteRepository;

    // ====== Helper para crear un gerente de prueba ======
    private Gerente crearGerenteTest(String sufijo) {
        Usuario u = new Usuario();
        u.setUsername("gerenteEliminarFinca_" + sufijo);
        u.setPasswordHash("{noop}pass");
        u.setRol(Rol.GERENTE);
        u.setActivo(true);
        u = usuarioService.guardar(u);

        Gerente g = new Gerente();
        g.setNombre("Gerente Eliminar Finca " + sufijo);
        g.setEmail("gerenteEliminarFinca" + sufijo + "@test.com");
        g.setUsuario(u);

        return gerenteRepository.save(g);
    }

    @Test
    void gerentePuedeEliminarFincaYDesapareceDelSistema() {
        // 1) Crear gerente
        Gerente gerente = crearGerenteTest("1");

        // 2) Crear y guardar una finca asociada a ese gerente
        Finca finca = new Finca();
        finca.setNombre("Finca para eliminar");
        finca.setEstado(EstadoFinca.SEMBRADA);
        finca.setCiudad("Ciudad Test");
        finca.setProvincia("Provincia Test");
        finca.setArea(10.5);
        finca.setGerente(gerente);

        finca = fincaService.guardar(finca);
        Long fincaId = finca.getId();
        assertNotNull(fincaId, "La finca guardada debe tener ID");

        // Comprobamos que existe antes de eliminar
        assertTrue(
                fincaService.encontrarPorId(fincaId).isPresent(),
                "La finca debería existir antes de ser eliminada"
        );

        // 3) Eliminar la finca
        fincaService.eliminarPorId(fincaId);

        // 4) Verificar que ya no aparece en el sistema
        assertTrue(
                fincaService.encontrarPorId(fincaId).isEmpty(),
                "La finca debería haberse eliminado definitivamente"
        );
    }
}
