package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.EstadoFinca;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.services.FincaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Controller
@RequestMapping("/fincas")
public class FincaController {

    private final FincaService fincaService;

    @Autowired
    public FincaController(FincaService fincaService) {
        this.fincaService = fincaService;
    }

    @GetMapping("/")
    public String listarFincas(Model model) {
        model.addAttribute("fincas", fincaService.encontrarTodas());
        return "fincas"; // templates/fincas.html
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Finca finca = fincaService.encontrarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        model.addAttribute("finca", finca);
        model.addAttribute("estados", Arrays.asList(EstadoFinca.values()));
        return "fincaForm"; // templates/fincaForm.html
    }

    @PostMapping("/{id}")
    public String actualizarFinca(@PathVariable Long id,
                                  @RequestParam("estado") EstadoFinca estado) {

        // Recuperamos la finca original de BD
        Finca finca = fincaService.encontrarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        // Solo modificamos el estado
        finca.setEstado(estado);

        fincaService.guardar(finca);

        return "redirect:/fincas/";
    }

}
