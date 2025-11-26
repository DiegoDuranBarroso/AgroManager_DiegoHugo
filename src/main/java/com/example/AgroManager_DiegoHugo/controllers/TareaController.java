package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.model.Tarea;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
import com.example.AgroManager_DiegoHugo.data.services.FincaService;
import com.example.AgroManager_DiegoHugo.data.services.TareaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/tareas")
public class TareaController {

    private final TareaService tareaService;
    private final EmpleadoService empleadoService;
    private final FincaService fincaService;

    @Autowired
    public TareaController(TareaService tareaService,
                           EmpleadoService empleadoService,
                           FincaService fincaService) {
        this.tareaService = tareaService;
        this.empleadoService = empleadoService;
        this.fincaService = fincaService;
    }

    @GetMapping("/")
    public String listarTareas(@RequestParam(name = "empleadoId", required = false) Long empleadoId,
                               Model model) {
        List<Tarea> tareas;
        if (empleadoId != null) {
            tareas = tareaService.tareasDeEmpleado(empleadoId);
            model.addAttribute("empleadoSeleccionado", empleadoId);
        } else {
            tareas = tareaService.encontrarTodas();
        }
        model.addAttribute("tareas", tareas);
        model.addAttribute("empleados", empleadoService.encontrarActivos());
        return "tareas"; // templates/tareas.html
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNuevaTarea(@RequestParam(name = "empleadoId", required = false) Long empleadoId,
                                              Model model) {
        model.addAttribute("tarea", new Tarea());
        model.addAttribute("empleados", empleadoService.encontrarActivos());
        model.addAttribute("fincas", fincaService.encontrarTodas());
        model.addAttribute("empleadoIdSeleccionado", empleadoId);
        return "tareaForm"; // templates/tareaForm.html
    }

    @PostMapping("/")
    public String registrarTarea(@RequestParam Long empleadoId,
                                 @RequestParam Long fincaId,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                 LocalDate fecha,
                                 @RequestParam String tipo,
                                 @RequestParam BigDecimal horas) {

        Empleado empleado = empleadoService.encontrarPorId(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));
        Finca finca = fincaService.encontrarPorId(fincaId)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        tareaService.registrarTarea(empleado, finca, fecha, tipo, horas);
        return "redirect:/tareas/?empleadoId=" + empleadoId;
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarTarea(@PathVariable Long id) {
        tareaService.eliminarPorId(id);
        return "redirect:/tareas/";
    }
}
