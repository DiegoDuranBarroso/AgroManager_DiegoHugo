package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.Contrato;
import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Gerente;
import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.model.TipoContrato;
import com.example.AgroManager_DiegoHugo.data.repositories.GerenteRepository;
import com.example.AgroManager_DiegoHugo.data.services.ContratoService;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
import com.example.AgroManager_DiegoHugo.data.services.UsuarioService;
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
    private final UsuarioService usuarioService;
    private final GerenteRepository gerenteRepository;

    @Autowired
    public ContratoController(ContratoService contratoService,
                              EmpleadoService empleadoService,
                              UsuarioService usuarioService,
                              GerenteRepository gerenteRepository) {
        this.contratoService = contratoService;
        this.empleadoService = empleadoService;
        this.usuarioService = usuarioService;
        this.gerenteRepository = gerenteRepository;
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

        // Añadimos gerente logueado (si lo hay) para mostrar su nombre en la vista
        usuarioService.obtenerUsuarioEnSesion().ifPresent(usuario -> {
            if (usuario.getRol() == Rol.GERENTE) {
                gerenteRepository.findByUsuarioId(usuario.getId())
                        .ifPresent(g -> model.addAttribute("gerente", g));
            }
        });

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

        // También añadimos el gerente para el navbar
        usuarioService.obtenerUsuarioEnSesion().ifPresent(usuario -> {
            if (usuario.getRol() == Rol.GERENTE) {
                gerenteRepository.findByUsuarioId(usuario.getId())
                        .ifPresent(g -> model.addAttribute("gerente", g));
            }
        });

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
