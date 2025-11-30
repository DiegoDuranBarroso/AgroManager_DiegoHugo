package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.Asignacion;
import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.model.Gerente;
import com.example.AgroManager_DiegoHugo.data.model.Rol;
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
    public String listarAsignaciones(Model model) {
        List<Asignacion> asignaciones = asignacionService.encontrarTodas();
        model.addAttribute("asignaciones", asignaciones);

        // Añadir gerente logueado (si lo hay) para mostrar su nombre en la vista
        usuarioService.obtenerUsuarioEnSesion().ifPresent(usuario -> {
            if (usuario.getRol() == Rol.GERENTE) {
                gerenteRepository.findByUsuarioId(usuario.getId())
                        .ifPresent(g -> model.addAttribute("gerente", g));
            }
        });

        return "asignaciones"; // templates/asignaciones.html
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
}
