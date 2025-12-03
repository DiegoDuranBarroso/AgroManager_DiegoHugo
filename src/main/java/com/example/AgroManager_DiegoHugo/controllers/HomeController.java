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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // ---- PÁGINA PRINCIPAL (home con el hero) ----
    @GetMapping("/")
    public String mostrarHome() {
        return "home";
    }

    // ---- LOGIN (AHORA USA index.html) ----
    // Tanto /login como /index mostrarán el mismo formulario (index.html)
    @GetMapping({"/login", "/index"})
    public String mostrarLogin(Model model) {

        // Si no hay rolSeleccionado (primera vez), ponemos GERENTE por defecto
        if (!model.containsAttribute("rolSeleccionado")) {
            model.addAttribute("rolSeleccionado", "GERENTE");
        }

        // 'error' puede venir como flashAttribute. Si no viene, no hace falta tocarlo.
        return "index";  // <-- el archivo se llama index.html
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String rol,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        username = (username == null) ? null : username.trim();
        password = (password == null) ? null : password.trim();

        Optional<Usuario> optUsuario = usuarioService.validarLogin(username, password);

        // Usuario no encontrado o inactivo
        if (optUsuario.isEmpty() || !optUsuario.get().isActivo()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Usuario o contraseña incorrectos, o usuario inactivo."
            );
            redirectAttributes.addFlashAttribute("rolSeleccionado", rol);
            return "redirect:/login";   // redirige al GET que renderiza index.html
        }

        Usuario usuario = optUsuario.get();

        // Rol del formulario no coincide con el rol guardado
        if (!usuario.getRol().name().equalsIgnoreCase(rol)) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Estás intentando iniciar sesión en un rol que no te corresponde."
            );
            redirectAttributes.addFlashAttribute("rolSeleccionado", rol);
            return "redirect:/login";
        }

        // Guardar el usuario en sesión
        session.setAttribute("usuarioId", usuario.getId());

        // Redirigimos a /home, que decide la vista según el rol
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
                                   RedirectAttributes redirectAttributes) {

        username = username.trim();

        if (usuarioService.encontrarPorUsername(username).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya está en uso.");
            redirectAttributes.addFlashAttribute("success", false);
            return "redirect:/registro/gerente";
        }

        if (telefono != null && telefono.isBlank()) {
            telefono = null;
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPasswordHash("{noop}" + password);
        usuario.setRol(Rol.GERENTE);
        usuario.setActivo(true);

        Usuario guardado = usuarioService.guardar(usuario);

        Gerente gerente = new Gerente();
        gerente.setNombre(nombre);
        gerente.setEmail(email);
        gerente.setTelefono(telefono);
        gerente.setUsuario(guardado);

        gerenteRepository.save(gerente);

        // Mensaje de éxito + redirección al index
        redirectAttributes.addFlashAttribute("registroOK", true);
        return "redirect:/index";
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
        // Después del logout, lo lógico es volver al login → index.html
        return "redirect:/login";
    }

}
