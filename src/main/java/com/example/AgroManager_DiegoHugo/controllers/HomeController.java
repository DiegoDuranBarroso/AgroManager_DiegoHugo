package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Gerente;
import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
import com.example.AgroManager_DiegoHugo.data.repositories.GerenteRepository;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
import com.example.AgroManager_DiegoHugo.data.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class HomeController {

    private final UsuarioService usuarioService;
    private final EmpleadoService empleadoService;
    private final GerenteRepository gerenteRepository;

    @Autowired
    public HomeController(UsuarioService usuarioService,
                           EmpleadoService empleadoService,
                           GerenteRepository gerenteRepository) {
        this.usuarioService = usuarioService;
        this.empleadoService = empleadoService;
        this.gerenteRepository = gerenteRepository;
    }

    @GetMapping({"/"})
    public String mostrarLogin(Model model) {
        // Para mostrar mensaje de error si vuelve del POST
        if (!model.containsAttribute("error")) {
            model.addAttribute("error", null);
        }
        return "index";  // templates/index.html será la pantalla de login
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam("usuarioId") Long usuarioId,
                                Model model) {

        Optional<Usuario> optUsuario = usuarioService.encontrarPorId(usuarioId);

        if (optUsuario.isEmpty() || !optUsuario.get().isActivo()) {
            model.addAttribute("error", "Usuario no encontrado o inactivo");
            return "index";
        }

        Usuario usuario = optUsuario.get();

        if (usuario.getRol() == Rol.GERENTE) {
            Optional<Gerente> optGerente = gerenteRepository.findByUsuarioId(usuario.getId());
            if (optGerente.isEmpty()) {
                model.addAttribute("error", "No se encontró el perfil de gerente asociado");
                return "index";
            }
            Gerente gerente = optGerente.get();
            model.addAttribute("gerente", gerente);
            return "gerenteHome";   // templates/gerenteHome.html
        } else if (usuario.getRol() == Rol.EMPLEADO) {
            Optional<Empleado> optEmpleado = empleadoService.encontrarPorUsuarioId(usuario.getId());
            if (optEmpleado.isEmpty()) {
                model.addAttribute("error", "No se encontró el perfil de empleado asociado");
                return "index";
            }
            Empleado empleado = optEmpleado.get();
            model.addAttribute("empleado", empleado);
            return "empleadoHome";  // templates/empleadoHome.html
        } else {
            model.addAttribute("error", "Rol de usuario no soportado");
            return "index";
        }
    }
}
