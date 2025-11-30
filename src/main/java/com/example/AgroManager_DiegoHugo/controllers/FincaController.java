package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.EstadoFinca;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
import com.example.AgroManager_DiegoHugo.data.model.Gerente;
import com.example.AgroManager_DiegoHugo.data.model.Rol;

import com.example.AgroManager_DiegoHugo.data.repositories.GerenteRepository;
import com.example.AgroManager_DiegoHugo.data.services.FincaService;
import com.example.AgroManager_DiegoHugo.data.services.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Controller
@RequestMapping("/fincas")
public class FincaController {

    private final FincaService fincaService;
    private final UsuarioService usuarioService;
    private final GerenteRepository gerenteRepository;

    @Autowired
    public FincaController(FincaService fincaService,
                           UsuarioService usuarioService,
                           GerenteRepository gerenteRepository) {
        this.fincaService = fincaService;
        this.usuarioService = usuarioService;
        this.gerenteRepository = gerenteRepository;
    }

    // ================== LISTAR FINCAS ==================
    @GetMapping("/")
    public String listarFincas(Model model) {

        model.addAttribute("fincas", fincaService.encontrarTodas());

        // ---- Cargar gerente para mostrar su nombre ----
        usuarioService.obtenerUsuarioEnSesion().ifPresent(usuario -> {
            if (usuario.getRol() == Rol.GERENTE) {
                gerenteRepository.findByUsuarioId(usuario.getId())
                        .ifPresent(g -> model.addAttribute("gerente", g));
            }
        });

        return "fincas"; // templates/fincas.html
    }

    // ================== FORMULARIO EDITAR ==================
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {

        Finca finca = fincaService.encontrarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        model.addAttribute("finca", finca);
        model.addAttribute("estados", Arrays.asList(EstadoFinca.values()));

        // ---- También añadir gerente aquí ----
        usuarioService.obtenerUsuarioEnSesion().ifPresent(usuario -> {
            if (usuario.getRol() == Rol.GERENTE) {
                gerenteRepository.findByUsuarioId(usuario.getId())
                        .ifPresent(g -> model.addAttribute("gerente", g));
            }
        });

        return "fincaForm"; // templates/fincaForm.html
    }

    // ================== POST EDITAR ==================
    @PostMapping("/{id}")
    public String actualizarFinca(
            @PathVariable Long id,
            @RequestParam("estado") EstadoFinca estado) {

        Finca finca = fincaService.encontrarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        finca.setEstado(estado);
        fincaService.guardar(finca);

        return "redirect:/fincas/";
    }

}
