package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Fichaje;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.model.Gerente;
import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
import com.example.AgroManager_DiegoHugo.data.repositories.GerenteRepository;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
import com.example.AgroManager_DiegoHugo.data.services.FichajeService;
import com.example.AgroManager_DiegoHugo.data.services.FincaService;
import com.example.AgroManager_DiegoHugo.data.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/fichajes")
public class FichajeController {

    private final FichajeService fichajeService;
    private final EmpleadoService empleadoService;
    private final FincaService fincaService;

    // NUEVOS CAMPOS
    private final UsuarioService usuarioService;
    private final GerenteRepository gerenteRepository;

    @Autowired
    public FichajeController(FichajeService fichajeService,
                             EmpleadoService empleadoService,
                             FincaService fincaService,
                             UsuarioService usuarioService,
                             GerenteRepository gerenteRepository) {
        this.fichajeService = fichajeService;
        this.empleadoService = empleadoService;
        this.fincaService = fincaService;
        this.usuarioService = usuarioService;
        this.gerenteRepository = gerenteRepository;
    }

    // ================= LISTADO (empleado o gerente) =================
    // Empleado: /fichajes/?empleadoId=X
    // Gerente:  /fichajes/  (sin empleadoId) -> ve todos

    @GetMapping("/")
    public String verFichajesEmpleado(
            @RequestParam(name = "empleadoId", required = false) Long empleadoId,
            Model model) {

        Usuario usuario = usuarioService.obtenerUsuarioEnSesion().orElse(null);

        // ===== GERENTE sin filtro → ver todos =====
        if (usuario != null && usuario.getRol() == Rol.GERENTE && empleadoId == null) {

            Gerente gerente = gerenteRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new IllegalStateException("Gerente no encontrado"));

            model.addAttribute("vistaGerente", true);
            model.addAttribute("gerente", gerente);

            model.addAttribute("fichajes", fichajeService.encontrarTodos());

            // PARA EL FILTRO
            model.addAttribute("empleados", empleadoService.encontrarActivos());
            model.addAttribute("empleadoSeleccionado", null);

            return "fichaje";
        }

        // ===== EMPLEADO o GERENTE filtrando por empleado =====
        if (empleadoId == null) {
            return "redirect:/home";
        }

        Empleado emp = empleadoService.encontrarPorId(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        model.addAttribute("empleado", emp);
        model.addAttribute("fichajes", fichajeService.fichajesDeEmpleado(empleadoId));

        // Si el que está logueado es gerente → vistaGerente activa
        if (usuario != null && usuario.getRol() == Rol.GERENTE) {

            Gerente gerente = gerenteRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new IllegalStateException("Gerente no encontrado"));

            model.addAttribute("vistaGerente", true);
            model.addAttribute("gerente", gerente);

            // para el filtro
            model.addAttribute("empleados", empleadoService.encontrarActivos());
            model.addAttribute("empleadoSeleccionado", empleadoId);
        }

        return "fichaje";
    }


    // ================= NUEVO FICHAJE (inicio, solo empleado) =================

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoFichaje(
            @RequestParam("empleadoId") Long empleadoId,
            Model model) {

        Empleado emp = empleadoService.encontrarPorId(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        Fichaje fichaje = new Fichaje();

        model.addAttribute("fichaje", fichaje);
        model.addAttribute("empleado", emp);
        model.addAttribute("fincas", fincaService.encontrarTodas());

        return "fichajeForm";
    }

    @PostMapping("/")
    public String guardarFichaje(@RequestParam Long empleadoId,
                                 @RequestParam Long fincaId) {

        Empleado empleado = empleadoService.encontrarPorId(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        Finca finca = fincaService.encontrarPorId(fincaId)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        fichajeService.iniciarFichaje(empleado, finca);

        return "redirect:/fichajes/?empleadoId=" + empleadoId;
    }

    // ================= MARCAR SALIDA (solo empleado) =================

    @PostMapping("/fin")
    public String registrarFin(@RequestParam Long empleadoId) {
        fichajeService.finalizarFichaje(empleadoId);
        return "redirect:/fichajes/?empleadoId=" + empleadoId;
    }

    // ================= ELIMINAR FICHAJE =================

    @PostMapping("/{id}/eliminar")
    public String eliminarFichaje(@PathVariable Long id) {

        Fichaje fichaje = fichajeService.fichajePorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Fichaje no encontrado"));

        Long empleadoId = (fichaje.getEmpleado() != null)
                ? fichaje.getEmpleado().getId()
                : null;

        fichajeService.eliminarPorId(id);

        if (empleadoId != null) {
            return "redirect:/fichajes/?empleadoId=" + empleadoId;
        }
        return "redirect:/home";
    }


}
