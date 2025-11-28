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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String mostrarFormularioGenerarNomina(
            @RequestParam(name = "empleadoId", required = false) Long empleadoId,
            Model model) {

        model.addAttribute("empleados", empleadoService.encontrarActivos());

        // Si venimos de una redirección con error, puede venir en el modelo como flash
        if (!model.containsAttribute("empleadoIdSeleccionado")) {
            model.addAttribute("empleadoIdSeleccionado", empleadoId);
        }

        return "nominaForm"; // templates/nominaForm.html
    }

    @PostMapping("/generar")
    public String generarNomina(@RequestParam Long empleadoId,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                LocalDate periodoInicio,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                LocalDate periodoFin,
                                @RequestParam(defaultValue = "BORRADOR") String estado,
                                RedirectAttributes redirectAttributes) {

        Empleado empleado = empleadoService.encontrarPorId(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        // Horas trabajadas en el periodo
        BigDecimal horas = tareaService.horasTrabajadasEntreFechas(empleadoId, periodoInicio, periodoFin);

        // Contratos vigentes en la fecha de fin
        List<Contrato> contratosVigentes = contratoService.contratosVigentesEn(empleadoId, periodoFin);

        if (contratosVigentes.isEmpty()) {
            // No hay contrato vigente -> volvemos al formulario con mensaje de error
            redirectAttributes.addFlashAttribute("errorNomina",
                    "No hay ningún contrato vigente para ese empleado en la fecha seleccionada. " +
                            "Revisa el periodo o crea un contrato primero.");
            redirectAttributes.addFlashAttribute("empleadoIdSeleccionado", empleadoId);
            return "redirect:/nominas/generar";
        }

        Contrato contrato = contratosVigentes.get(0);

        // Cálculo simplificado del total bruto: salarioBase + horas * tarifaHora
        BigDecimal totalBruto = contrato.getSalarioBase()
                .add(contrato.getTarifaHora().multiply(horas));

        nominaService.generarNomina(empleado, periodoInicio, periodoFin, totalBruto, estado);

        redirectAttributes.addFlashAttribute("mensajeNomina",
                "Nómina generada correctamente para " + empleado.getNombre());

        return "redirect:/nominas/?empleadoId=" + empleadoId;
    }
}
