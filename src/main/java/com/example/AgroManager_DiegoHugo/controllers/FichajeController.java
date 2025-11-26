package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
import com.example.AgroManager_DiegoHugo.data.services.FichajeService;
import com.example.AgroManager_DiegoHugo.data.services.FincaService;
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

    @Autowired
    public FichajeController(FichajeService fichajeService,
                             EmpleadoService empleadoService,
                             FincaService fincaService) {
        this.fichajeService = fichajeService;
        this.empleadoService = empleadoService;
        this.fincaService = fincaService;
    }

    @GetMapping("/")
    public String verEstadoFichaje(@RequestParam Long empleadoId, Model model) {
        Empleado empleado = empleadoService.encontrarPorId(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        model.addAttribute("empleado", empleado);
        model.addAttribute("fichajeAbierto", fichajeService.fichajeAbierto(empleadoId).orElse(null));
        model.addAttribute("fincas", fincaService.encontrarTodas());
        return "fichaje"; // templates/fichaje.html
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
