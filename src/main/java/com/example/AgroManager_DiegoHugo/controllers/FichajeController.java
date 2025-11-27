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


    @GetMapping("/")
    public String verEstadoFichaje(
            @RequestParam(name = "empleadoId", required = false) Long empleadoId,
            Model model) {

        model.addAttribute("empleados", empleadoService.encontrarTodos());
        model.addAttribute("empleadoSeleccionado", empleadoId);

        List<Fichaje> fichajes;
        if (empleadoId != null) {
            fichajes = fichajeService.fichajesDeEmpleado(empleadoId);
        } else {
            fichajes = fichajeService.fichajesTodos();
        }

        model.addAttribute("fichajes", fichajes);

        return "fichaje";   // templates/fichaje.html
    }


    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoFichaje(Model model) {

        Fichaje fichaje = new Fichaje();   // entidad vacía

        model.addAttribute("fichaje", fichaje);
        model.addAttribute("empleados", empleadoService.encontrarTodos());
        model.addAttribute("fincas", fincaService.encontrarTodas());

        return "fichajeForm";  // templates/fichajeForm.html
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

    @PostMapping("/{id}/salida")
    public String marcarSalida(@PathVariable Long id) {

        Fichaje fichaje = fichajeService.fichajePorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Fichaje no encontrado"));

        Long empleadoId = (fichaje.getEmpleado() != null) ? fichaje.getEmpleado().getId() : null;

        fichajeService.finalizarFichaje(fichaje.getEmpleado().getId());

        if (empleadoId != null) {
            return "redirect:/fichajes/?empleadoId=" + empleadoId;
        }
        return "redirect:/fichajes/";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarFichaje(@PathVariable Long id) {

        Fichaje fichaje = fichajeService.fichajePorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Fichaje no encontrado"));

        Long empleadoId = (fichaje.getEmpleado() != null) ? fichaje.getEmpleado().getId() : null;

        fichajeService.eliminarPorId(id);

        if (empleadoId != null) {
            return "redirect:/fichajes/?empleadoId=" + empleadoId;
        }
        return "redirect:/fichajes/";
    }


    @PostMapping("/inicio")
    public String registrarInicio(@RequestParam Long empleadoId,
                                  @RequestParam Long fincaId) {
        Empleado empleado = empleadoService.encontrarPorId(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));
        Finca finca = fincaService.encontrarPorId(fincaId)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        fichajeService.iniciarFichaje(empleado, finca);
        return "redirect:/fichajes/?empleadoId=" + empleadoId;
    }

    @PostMapping("/fin")
    public String registrarFin(@RequestParam Long empleadoId) {
        fichajeService.finalizarFichaje(empleadoId);
        return "redirect:/fichajes/?empleadoId=" + empleadoId;
    }
}
