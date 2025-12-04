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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/fichajes")
public class FichajeController {

    private final FichajeService fichajeService;
    private final EmpleadoService empleadoService;
    private final FincaService fincaService;

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
            @RequestParam(name = "salidaOK", required = false) Boolean salidaOK,
            Model model) {

        Usuario usuario = usuarioService.obtenerUsuarioEnSesion().orElse(null);

        // Flag para mostrar el modal de "salida registrada"
        model.addAttribute("salidaOK", Boolean.TRUE.equals(salidaOK));

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

    @PostMapping("/nuevo")
    public String guardarFichaje(@RequestParam Long empleadoId,
                                 @RequestParam Long fincaId,
                                 RedirectAttributes redirectAttributes) {

        // Cargamos empleado y finca
        Empleado empleado = empleadoService.encontrarPorId(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        Finca finca = fincaService.encontrarPorId(fincaId)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        try {
            // Aquí puede saltar IllegalStateException si ya tiene un fichaje abierto
            fichajeService.iniciarFichaje(empleado, finca);

            // Si todo va bien, redirigimos normal
            return "redirect:/fichajes/?empleadoId=" + empleadoId + "&modo=EMPLEADO";

        } catch (IllegalStateException ex) {
            // En vez de 500, guardamos el mensaje en flash y redirigimos igual
            redirectAttributes.addFlashAttribute("errorFichaje", ex.getMessage());
            return "redirect:/fichajes/?empleadoId=" + empleadoId + "&modo=EMPLEADO";
        }
    }


    // ================= MARCAR SALIDA (solo empleado) =================

    @PostMapping("/fin")
    public String registrarFin(@RequestParam Long empleadoId) {
        fichajeService.finalizarFichaje(empleadoId);
        // añadimos salidaOK=true para que salga el modal
        return "redirect:/fichajes/?empleadoId=" + empleadoId + "&salidaOK=true";
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

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditarFichaje(@PathVariable Long id, Model model) {

        Fichaje fichaje = fichajeService.fichajePorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Fichaje no encontrado"));

        Usuario usuario = usuarioService.obtenerUsuarioEnSesion()
                .orElseThrow(() -> new IllegalStateException("No hay usuario en sesión"));

        if (usuario.getRol() != Rol.GERENTE) {
            return "redirect:/home";
        }

        Gerente gerente = gerenteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new IllegalStateException("Gerente no encontrado"));

        model.addAttribute("fichaje", fichaje);
        model.addAttribute("empleados", empleadoService.encontrarActivos());
        model.addAttribute("fincas", fincaService.encontrarTodas());
        model.addAttribute("gerente", gerente);

        return "fichajeEditar";
    }
    

    @PostMapping("/{id}/editar")
    public String actualizarFichaje(@PathVariable Long id,
                                    @RequestParam Long empleadoId,
                                    @RequestParam Long fincaId,
                                    @RequestParam String inicio,
                                    @RequestParam(required = false) String fin,
                                    @RequestParam String estado) {

        Fichaje fichaje = fichajeService.fichajePorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Fichaje no encontrado"));

        Empleado empleado = empleadoService.encontrarPorId(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        Finca finca = fincaService.encontrarPorId(fincaId)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        fichaje.setEmpleado(empleado);
        fichaje.setFinca(finca);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        ZoneId zoneId = ZoneId.systemDefault();

        // Parsear inicio
        LocalDateTime inicioLdt = LocalDateTime.parse(inicio, formatter);
        fichaje.setInicio(inicioLdt.atZone(zoneId).toInstant());

        // Parsear fin si viene
        if (fin != null && !fin.isBlank()) {
            LocalDateTime finLdt = LocalDateTime.parse(fin, formatter);
            fichaje.setFin(finLdt.atZone(zoneId).toInstant());
        } else {
            fichaje.setFin(null);
        }

        fichaje.setEstado(estado);

        fichajeService.guardar(fichaje);

        return "redirect:/fichajes/";
    }




}
