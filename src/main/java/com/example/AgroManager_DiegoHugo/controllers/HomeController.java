package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Gerente;
import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
import com.example.AgroManager_DiegoHugo.data.repositories.GerenteRepository;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
import com.example.AgroManager_DiegoHugo.data.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
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

    // ---- PÁGINA PRINCIPAL (menú con botones) ----
    @GetMapping("/")
    public String mostrarHome() {
        return "home";
    }

    @GetMapping("/index")
    public String menuInicial() {
        return "index";
    }

    // ---- LOGIN ÚNICO (Gerente / Empleado en un select) ----
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        if (!model.containsAttribute("error")) {
            model.addAttribute("error", null);
        }
        // rol por defecto GERENTE (para que aparezca seleccionado)
        if (!model.containsAttribute("rolSeleccionado")) {
            model.addAttribute("rolSeleccionado", "GERENTE");
        }
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String rol,
                                Model model,
                                HttpSession session) {

        username = (username == null) ? null : username.trim();
        password = (password == null) ? null : password.trim();

        Optional<Usuario> optUsuario = usuarioService.validarLogin(username, password);

        if (optUsuario.isEmpty() || !optUsuario.get().isActivo()) {
            model.addAttribute("error", "Usuario o contraseña incorrectos, o usuario inactivo.");
            model.addAttribute("rolSeleccionado", rol);
            return "login";
        }

        Usuario usuario = optUsuario.get();

        // Comprobar que el rol del login coincide con el rol guardado
        if (!usuario.getRol().name().equalsIgnoreCase(rol)) {
            model.addAttribute("error", "Estás intentando iniciar sesión en un rol que no te corresponde.");
            model.addAttribute("rolSeleccionado", rol);
            return "login";
        }

        // Guardar el usuario en sesión
        session.setAttribute("usuarioId", usuario.getId());

        // Redirigimos a /home, que decide según el rol
        return "redirect:/home";
    }

    // ---- REGISTRO DE GERENTE (GET) ----
    @GetMapping("/registro/gerente")
    public String mostrarFormularioRegistroGerente(Model model) {
        if (!model.containsAttribute("error")) {
            model.addAttribute("error", null);
        }
        if (!model.containsAttribute("success")) {
            model.addAttribute("success", false);
        }
        return "registroGerente";
    }

    // ---- REGISTRO DE GERENTE (POST) ----
    @PostMapping("/registro/gerente")
    public String registrarGerente(@RequestParam String nombre,
                                   @RequestParam String email,
                                   @RequestParam(required = false) String telefono,
                                   @RequestParam String username,
                                   @RequestParam String password,
                                   Model model) {

        username = username.trim();

        if (usuarioService.encontrarPorUsername(username).isPresent()) {
            model.addAttribute("error", "El nombre de usuario ya está en uso.");
            model.addAttribute("success", false);
            return "registroGerente";
        }

        if (telefono != null && telefono.isBlank()) {
            telefono = null;
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPasswordHash("{noop}" + password); // solo para prototipo
        usuario.setRol(Rol.GERENTE);
        usuario.setActivo(true);

        Usuario guardado = usuarioService.guardar(usuario);

        Gerente gerente = new Gerente();
        gerente.setNombre(nombre);
        gerente.setEmail(email);
        gerente.setTelefono(telefono);
        gerente.setUsuario(guardado);

        gerenteRepository.save(gerente);

        model.addAttribute("success", true);
        model.addAttribute("error", null);

        return "registroGerente";
    }

    // ---- HOME SEGÚN ROL ----
    @GetMapping("/home")
    public String redirigirSegunRol(Model model) {

        Optional<Usuario> usuarioLogueado = usuarioService.obtenerUsuarioEnSesion();

        if (usuarioLogueado.isEmpty()) {
            return "redirect:/";
        }

        Usuario usuario = usuarioLogueado.get();

        if (usuario.getRol() == Rol.GERENTE) {
            Gerente g = gerenteRepository.findByUsuarioId(usuario.getId()).orElse(null);
            model.addAttribute("gerente", g);
            return "gerenteHome";
        } else if (usuario.getRol() == Rol.EMPLEADO) {
            Empleado e = empleadoService.encontrarPorUsuarioId(usuario.getId()).orElse(null);
            model.addAttribute("empleado", e);
            return "empleadoHome";
        }

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/index";
    }

}
