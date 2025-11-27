package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.Asignacion;
import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.services.AsignacionService;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
import com.example.AgroManager_DiegoHugo.data.services.FincaService;
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

    @Autowired
    public AsignacionController(AsignacionService asignacionService,
                                EmpleadoService empleadoService,
                                FincaService fincaService) {
        this.asignacionService = asignacionService;
        this.empleadoService = empleadoService;
        this.fincaService = fincaService;
    }

    @GetMapping("/")
    public String listarAsignaciones(Model model) {
        List<Asignacion> asignaciones = asignacionService.encontrarTodas();
        model.addAttribute("asignaciones", asignaciones);
        return "asignaciones"; // templates/asignaciones.html
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNuevaAsignacion(Model model) {
        model.addAttribute("asignacion", new Asignacion());
        model.addAttribute("empleados", empleadoService.encontrarActivos());
        model.addAttribute("fincas", fincaService.encontrarTodas());
        return "asignacionForm"; // templates/asignacionForm.html
    }

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

    @PostMapping("/{id}/cerrar")
    public String cerrarAsignacion(@PathVariable Long id,
                                   @RequestParam(required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                   LocalDate fechaFin) {
        asignacionService.cerrarAsignacion(id, fechaFin);
        return "redirect:/asignaciones/";
    }


}
