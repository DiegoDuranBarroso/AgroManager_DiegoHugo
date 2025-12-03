package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.model.Tarea;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
import com.example.AgroManager_DiegoHugo.data.repositories.GerenteRepository;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
import com.example.AgroManager_DiegoHugo.data.services.FincaService;
import com.example.AgroManager_DiegoHugo.data.services.TareaService;
import com.example.AgroManager_DiegoHugo.data.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tareas")
public class TareaController {

    private final TareaService tareaService;
    private final EmpleadoService empleadoService;
    private final FincaService fincaService;
    private final UsuarioService usuarioService;
    private final GerenteRepository gerenteRepository;

    @Autowired
    public TareaController(TareaService tareaService,
                           EmpleadoService empleadoService,
                           FincaService fincaService,
                           UsuarioService usuarioService,
                           GerenteRepository gerenteRepository) {
        this.tareaService = tareaService;
        this.empleadoService = empleadoService;
        this.fincaService = fincaService;
        this.usuarioService = usuarioService;
        this.gerenteRepository = gerenteRepository;
    }

    // ========= LISTADO =========
    // GERENTE:  /tareas/                      -> ve todas y puede filtrar por empleado
    // EMPLEADO: /tareas/?empleadoId=X&modo=EMPLEADO -> solo sus tareas, sin filtro

    @GetMapping("/")
    public String listarTareas(@RequestParam(name = "empleadoId", required = false) Long empleadoId,
                               @RequestParam(name = "modo", required = false) String modo,
                               Model model) {

        List<Tarea> tareas;
        boolean mostrarFiltro;

        // ===== VISTA EMPLEADO =====
        if ("EMPLEADO".equalsIgnoreCase(modo) && empleadoId != null) {
            tareas = tareaService.tareasDeEmpleado(empleadoId);
            mostrarFiltro = false;

            empleadoService.encontrarPorId(empleadoId).ifPresent(e -> {
                model.addAttribute("empleado", e);
                model.addAttribute("empleadoSesion", e);
            });

        } else {
            // ===== VISTA GERENTE =====
            if (empleadoId != null) {
                tareas = tareaService.tareasDeEmpleado(empleadoId);
                model.addAttribute("empleadoSeleccionado", empleadoId);
            } else {
                tareas = tareaService.encontrarTodas();
            }
            model.addAttribute("empleados", empleadoService.encontrarTodos());
            mostrarFiltro = true;
        }

        model.addAttribute("tareas", tareas);
        model.addAttribute("mostrarFiltro", mostrarFiltro);
        model.addAttribute("modo", modo);

        // ===== USUARIO EN SESIÓN PARA EL NAVBAR (gerente o empleado) =====
        Optional<Usuario> optUsuario = usuarioService.obtenerUsuarioEnSesion();
        if (optUsuario.isPresent()) {
            Usuario u = optUsuario.get();
            if (u.getRol() == Rol.GERENTE) {
                gerenteRepository.findByUsuarioId(u.getId())
                        .ifPresent(g -> model.addAttribute("gerente", g));
            } else if (u.getRol() == Rol.EMPLEADO) {
                empleadoService.encontrarPorUsuarioId(u.getId())
                        .ifPresent(e -> model.addAttribute("empleadoSesion", e));
            }
        }

        return "tareas";   // templates/tareas.html
    }

    // ========= NUEVA TAREA =========

    @GetMapping("/nueva")
    public String mostrarFormularioNuevaTarea(
            @RequestParam(name = "empleadoId", required = false) Long empleadoId,
            @RequestParam(name = "modo", required = false) String modo,
            Model model) {

        model.addAttribute("modo", modo);
        model.addAttribute("fincas", fincaService.encontrarTodas());

        if ("EMPLEADO".equalsIgnoreCase(modo) && empleadoId != null) {
            Empleado emp = empleadoService.encontrarPorId(empleadoId)
                    .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));
            model.addAttribute("empleadoActual", emp);
            model.addAttribute("empleados", null);
        } else {
            model.addAttribute("empleadoActual", null);
            model.addAttribute("empleados", empleadoService.encontrarTodos());
        }

        // Usuario en sesión para navbar
        Optional<Usuario> optUsuario = usuarioService.obtenerUsuarioEnSesion();
        if (optUsuario.isPresent()) {
            Usuario u = optUsuario.get();
            if (u.getRol() == Rol.GERENTE) {
                gerenteRepository.findByUsuarioId(u.getId())
                        .ifPresent(g -> model.addAttribute("gerente", g));
            } else if (u.getRol() == Rol.EMPLEADO) {
                empleadoService.encontrarPorUsuarioId(u.getId())
                        .ifPresent(e -> model.addAttribute("empleadoSesion", e));
            }
        }

        return "tareaForm";
    }

    // ========= GUARDAR TAREA =========

    @PostMapping("/")
    public String guardarTarea(@RequestParam Long empleadoId,
                               @RequestParam Long fincaId,
                               @RequestParam String tipo,
                               @RequestParam BigDecimal horas,
                               @RequestParam(name = "modo", required = false) String modo) {

        Empleado empleado = empleadoService.encontrarPorId(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));
        Finca finca = fincaService.encontrarPorId(fincaId)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        tareaService.registrarTarea(empleado, finca, LocalDate.now(), tipo, horas);

        if ("EMPLEADO".equalsIgnoreCase(modo)) {
            return "redirect:/tareas/?empleadoId=" + empleadoId + "&modo=EMPLEADO";
        }
        return "redirect:/tareas/";
    }

    // ========= ELIMINAR TAREA (solo GERENTE) =========

    @PostMapping("/{id}/eliminar")
    public String eliminarTarea(@PathVariable Long id) {
        tareaService.eliminarPorId(id);
        return "redirect:/tareas/";
    }

    // ========= MARCAR REALIZADO (EMPLEADO) =========

    @PostMapping("/{id}/realizado")
    public String marcarRealizado(@PathVariable Long id,
                                  RedirectAttributes redirectAttributes) {

        Tarea tarea = tareaService.marcarRealizada(id);
        Long empleadoId = tarea.getEmpleado().getId();

        // Flash attribute para mostrar el overlay “guay”
        redirectAttributes.addFlashAttribute("tareaRealizadaOK", true);

        return "redirect:/tareas/?modo=EMPLEADO&empleadoId=" + empleadoId;
    }

}
