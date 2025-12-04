package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.*;
import com.example.AgroManager_DiegoHugo.data.repositories.GerenteRepository;
import com.example.AgroManager_DiegoHugo.data.services.AsignacionService;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
import com.example.AgroManager_DiegoHugo.data.services.FincaService;
import com.example.AgroManager_DiegoHugo.data.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/asignaciones")
public class AsignacionController {

    private final AsignacionService asignacionService;
    private final EmpleadoService empleadoService;
    private final FincaService fincaService;
    private final UsuarioService usuarioService;
    private final GerenteRepository gerenteRepository;

    @Autowired
    public AsignacionController(AsignacionService asignacionService,
                                EmpleadoService empleadoService,
                                FincaService fincaService,
                                UsuarioService usuarioService,
                                GerenteRepository gerenteRepository) {
        this.asignacionService = asignacionService;
        this.empleadoService = empleadoService;
        this.fincaService = fincaService;
        this.usuarioService = usuarioService;
        this.gerenteRepository = gerenteRepository;
    }

    // ===== LISTAR ASIGNACIONES =====
    @GetMapping("/")
    public String listarAsignaciones(
            @RequestParam(name = "empleadoId", required = false) Long empleadoId,
            Model model) {

        // Lista de asignaciones (filtradas o no)
        List<Asignacion> asignaciones;

        if (empleadoId != null) {
            // Necesitas este método en el service/repo
            asignaciones = asignacionService.encontrarPorEmpleadoId(empleadoId);
            model.addAttribute("empleadoSeleccionado", empleadoId);
        } else {
            asignaciones = asignacionService.encontrarTodas();
        }

        model.addAttribute("asignaciones", asignaciones);

        // 👉 MUY IMPORTANTE: cargar empleados para el <select>
        model.addAttribute("empleados", empleadoService.encontrarTodos());
        // o si tienes solo activos:
        // model.addAttribute("empleados", empleadoService.encontrarActivos());

        // Nombre del gerente para el navbar (como ya tenías)
        usuarioService.obtenerUsuarioEnSesion().ifPresent(usuario -> {
            if (usuario.getRol() == Rol.GERENTE) {
                gerenteRepository.findByUsuarioId(usuario.getId())
                        .ifPresent(g -> model.addAttribute("gerente", g));
            }
        });

        return "asignaciones"; // asignaciones.html
    }


    // ===== FORMULARIO NUEVA ASIGNACIÓN =====
    @GetMapping("/nueva")
    public String mostrarFormularioNuevaAsignacion(Model model) {
        model.addAttribute("asignacion", new Asignacion());
        model.addAttribute("empleados", empleadoService.encontrarActivos());
        model.addAttribute("fincas", fincaService.encontrarTodas());

        // También aquí añadimos el gerente para el navbar
        usuarioService.obtenerUsuarioEnSesion().ifPresent(usuario -> {
            if (usuario.getRol() == Rol.GERENTE) {
                gerenteRepository.findByUsuarioId(usuario.getId())
                        .ifPresent(g -> model.addAttribute("gerente", g));
            }
        });

        return "asignacionForm"; // templates/asignacionForm.html
    }

    // ===== CREAR ASIGNACIÓN =====
    @PostMapping("/")
    public String crearAsignacion(@RequestParam Long empleadoId,
                                  @RequestParam Long fincaId,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                  LocalDate fechaInicio,
                                  @RequestParam(required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                  LocalDate fechaFin) {

        Empleado empleado = empleadoService.encontrarPorId(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));
        Finca finca = fincaService.encontrarPorId(fincaId)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        asignacionService.asignarEmpleadoAFinca(empleado, finca, fechaInicio, fechaFin);
        return "redirect:/asignaciones/";
    }

    // ===== CERRAR ASIGNACIÓN =====
    @PostMapping("/{id}/cerrar")
    public String cerrarAsignacion(@PathVariable Long id,
                                   @RequestParam(required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                   LocalDate fechaFin) {
        asignacionService.cerrarAsignacion(id, fechaFin);
        return "redirect:/asignaciones/";
    }

    // ===== ELIMINAR ASIGNACIÓN =====
    @PostMapping("/{id}/eliminar")
    public String eliminarAsignacion(@PathVariable Long id) {
        asignacionService.eliminarAsignacion(id);
        return "redirect:/asignaciones/";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditarAsignacion(@PathVariable Long id, Model model) {

        // Usuario en sesión
        Usuario usuario = usuarioService.obtenerUsuarioEnSesion()
                .orElseThrow(() -> new IllegalStateException("No hay usuario en sesión"));

        // Solo GERENTE edita asignaciones
        if (usuario.getRol() != Rol.GERENTE) {
            return "redirect:/home";
        }

        Gerente gerente = gerenteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new IllegalStateException("Gerente no encontrado"));

        Asignacion asignacion = asignacionService.encontrarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada"));

        model.addAttribute("asignacion", asignacion);
        model.addAttribute("gerente", gerente);
        model.addAttribute("empleados", empleadoService.encontrarActivos()); // o encontrarTodos()
        model.addAttribute("fincas", fincaService.encontrarTodas());

        return "asignacionEditar";  // templates/asignacionEditar.html
    }

    @PostMapping("/{id}")
    public String actualizarAsignacion(
            @PathVariable Long id,
            @RequestParam Long empleadoId,
            @RequestParam Long fincaId,
            @RequestParam String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) java.math.BigDecimal horasEstimadas
    ) {
        // Usuario en sesión
        Usuario usuario = usuarioService.obtenerUsuarioEnSesion()
                .orElseThrow(() -> new IllegalStateException("No hay usuario en sesión"));

        if (usuario.getRol() != Rol.GERENTE) {
            return "redirect:/home";
        }

        Asignacion asignacion = asignacionService.encontrarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada"));

        Empleado empleado = empleadoService.encontrarPorId(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        Finca finca = fincaService.encontrarPorId(fincaId)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        asignacion.setEmpleado(empleado);
        asignacion.setFinca(finca);
        asignacion.setFechaInicio(LocalDate.parse(fechaInicio));

        if (fechaFin != null && !fechaFin.isBlank()) {
            asignacion.setFechaFin(LocalDate.parse(fechaFin));
        } else {
            asignacion.setFechaFin(null);
        }


        asignacionService.guardar(asignacion);  // método que veremos ahora

        return "redirect:/asignaciones/";
    }


}
