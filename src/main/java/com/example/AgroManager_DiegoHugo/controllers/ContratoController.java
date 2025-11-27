package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.Contrato;
import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.TipoContrato;
import com.example.AgroManager_DiegoHugo.data.services.ContratoService;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/contratos")
public class ContratoController {

    private final ContratoService contratoService;
    private final EmpleadoService empleadoService;

    @Autowired
    public ContratoController(ContratoService contratoService,
                              EmpleadoService empleadoService) {
        this.contratoService = contratoService;
        this.empleadoService = empleadoService;
    }

    @GetMapping("/")
    public String listarContratos(@RequestParam(name = "empleadoId", required = false) Long empleadoId,
                                  Model model) {
        List<Contrato> contratos;
        if (empleadoId != null) {
            contratos = contratoService.encontrarPorEmpleado(empleadoId);
            model.addAttribute("empleadoSeleccionado", empleadoId);
        } else {
            contratos = contratoService.encontrarTodos();
        }

        model.addAttribute("contratos", contratos);
        model.addAttribute("empleados", empleadoService.encontrarActivos());
        return "contratos"; // templates/contratos.html
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoContrato(
            @RequestParam(name = "empleadoId", required = false) Long empleadoId,
            Model model) {

        model.addAttribute("contrato", new Contrato());
        model.addAttribute("empleados", empleadoService.encontrarActivos());
        model.addAttribute("tiposContrato", Arrays.asList(TipoContrato.values()));
        model.addAttribute("empleadoIdSeleccionado", empleadoId);

        return "contratoForm"; // templates/contratoForm.html
    }

    @PostMapping("/guardar")
    public String crearContrato(@RequestParam("empleadoId") Long empleadoId,
                                @RequestParam("tipo") TipoContrato tipo,
                                @RequestParam("fechaInicio")
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                LocalDate fechaInicio,
                                @RequestParam(value = "fechaFin", required = false)
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                LocalDate fechaFin,
                                @RequestParam("salarioBase") BigDecimal salarioBase,
                                @RequestParam("tarifaHora") BigDecimal tarifaHora) {

        Empleado empleado = empleadoService.encontrarPorId(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        contratoService.crearContrato(empleado, tipo, fechaInicio, fechaFin, salarioBase, tarifaHora);

        return "redirect:/contratos/?empleadoId=" + empleadoId;
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarContrato(@PathVariable Long id) {
        contratoService.eliminarPorId(id);
        return "redirect:/contratos/";
    }
}
