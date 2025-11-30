package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Fichaje;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
import com.example.AgroManager_DiegoHugo.data.services.FichajeService;
import com.example.AgroManager_DiegoHugo.data.services.FincaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/fichajes")
public class FichajeController {

    private final FichajeService fichajeService;
    private final EmpleadoService empleadoService;
    private final FincaService fincaService;

    @Autowired
    public FichajeController(FichajeService fichajeService,
                             EmpleadoService empleadoService,
                             FincaService fincaService) {
        this.fichajeService = fichajeService;
        this.empleadoService = empleadoService;
        this.fincaService = fincaService;
    }

    // ================= LISTADO (solo empleado) =================
    // Llamado desde empleadoHome:  /fichajes/?empleadoId=X

    @GetMapping("/")
    public String verFichajesEmpleado(
            @RequestParam(name = "empleadoId", required = false) Long empleadoId,
            Model model) {

        if (empleadoId == null) {
            // Si alguien entra sin empleadoId, lo mandamos al “home”
            return "redirect:/home";
        }

        Empleado emp = empleadoService.encontrarPorId(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        List<Fichaje> fichajes = fichajeService.fichajesDeEmpleado(empleadoId);

        model.addAttribute("empleado", emp);
        model.addAttribute("fichajes", fichajes);

        return "fichaje";   // templates/fichaje.html (versión solo empleado)
    }

    // ================= NUEVO FICHAJE (inicio, solo empleado) =================
    // Llamado desde empleadoHome: /fichajes/nuevo?empleadoId=X

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoFichaje(
            @RequestParam("empleadoId") Long empleadoId,
            Model model) {

        Empleado emp = empleadoService.encontrarPorId(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        Fichaje fichaje = new Fichaje();

        model.addAttribute("fichaje", fichaje);
        model.addAttribute("empleado", emp);                  // solo este empleado
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

        // Volver al listado de ese empleado
        return "redirect:/fichajes/?empleadoId=" + empleadoId;
    }

    // ================= MARCAR SALIDA (solo empleado) =================
    // Llamado desde la tabla de fichajes: /fichajes/fin?empleadoId=X

    @PostMapping("/fin")
    public String registrarFin(@RequestParam Long empleadoId) {
        fichajeService.finalizarFichaje(empleadoId);
        return "redirect:/fichajes/?empleadoId=" + empleadoId;
    }

    // ================= ELIMINAR FICHAJE (opcional, p.ej. para gerente) =================
    // Si más adelante tienes una vista de gerente con borrado, reutilizas esto.

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
        // Si no sabemos a qué empleado volver, mandamos al home
        return "redirect:/home";
    }
}
