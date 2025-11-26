package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.Contrato;
import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Nomina;
import com.example.AgroManager_DiegoHugo.data.services.ContratoService;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
import com.example.AgroManager_DiegoHugo.data.services.NominaService;
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
@RequestMapping("/nominas")
public class NominaController {

    private final NominaService nominaService;
    private final EmpleadoService empleadoService;
    private final TareaService tareaService;
    private final ContratoService contratoService;

    @Autowired
    public NominaController(NominaService nominaService,
                            EmpleadoService empleadoService,
                            TareaService tareaService,
                            ContratoService contratoService) {
        this.nominaService = nominaService;
        this.empleadoService = empleadoService;
        this.tareaService = tareaService;
        this.contratoService = contratoService;
    }

    @GetMapping("/")
    public String listarNominas(@RequestParam(name = "empleadoId", required = false) Long empleadoId,
                                Model model) {
        List<Nomina> nominas;
        if (empleadoId != null) {
            nominas = nominaService.nominasDeEmpleado(empleadoId);
            model.addAttribute("empleadoSeleccionado", empleadoId);
        } else {
            nominas = nominaService.encontrarTodas();
        }
        model.addAttribute("nominas", nominas);
        model.addAttribute("empleados", empleadoService.encontrarActivos());
        return "nominas"; // templates/nominas.html
    }

    @GetMapping("/generar")
    public String mostrarFormularioGenerarNomina(@RequestParam(name = "empleadoId", required = false) Long empleadoId,
                                                 Model model) {
        model.addAttribute("empleados", empleadoService.encontrarActivos());
        model.addAttribute("empleadoIdSeleccionado", empleadoId);
        return "nominaForm"; // templates/nominaForm.html
    }

    @PostMapping("/generar")
    public String generarNomina(@RequestParam Long empleadoId,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                LocalDate periodoInicio,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                LocalDate periodoFin,
                                @RequestParam(defaultValue = "BORRADOR") String estado) {

        Empleado empleado = empleadoService.encontrarPorId(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        // Horas trabajadas en el periodo
        BigDecimal horas = tareaService.horasTrabajadasEntreFechas(empleadoId, periodoInicio, periodoFin);

        // Cogemos contrato vigente en el periodo de fin (por ejemplo)
        Contrato contrato = contratoService.contratosVigentesEn(empleadoId, periodoFin).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay contrato vigente para el empleado en la fecha seleccionada"));

        // Cálculo simplificado del total bruto: salarioBase + horas * tarifaHora
        BigDecimal totalBruto = contrato.getSalarioBase()
                .add(contrato.getTarifaHora().multiply(horas));

        nominaService.generarNomina(empleado, periodoInicio, periodoFin, totalBruto, estado);

        return "redirect:/nominas/?empleadoId=" + empleadoId;
    }
}
