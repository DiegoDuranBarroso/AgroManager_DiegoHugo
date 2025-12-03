package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.EstadoFinca;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.model.Gerente;
import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
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

    // ================== NUEVA FINCA (GET) ==================
    @GetMapping("/nueva")
    public String mostrarFormularioNuevaFinca(Model model) {

        // Obtener gerente en sesión
        Usuario usuario = usuarioService.obtenerUsuarioEnSesion()
                .orElseThrow(() -> new IllegalStateException("No hay usuario en sesión"));

        if (usuario.getRol() != Rol.GERENTE) {
            // Por seguridad, redirigimos
            return "redirect:/home";
        }

        Gerente gerente = gerenteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new IllegalStateException("Gerente no encontrado"));

        model.addAttribute("finca", new Finca());
        model.addAttribute("estados", Arrays.asList(EstadoFinca.values()));
        model.addAttribute("gerente", gerente);

        return "fincaNueva"; // nueva vista para crear fincas
    }

    // ================== NUEVA FINCA (POST) ==================
    @PostMapping("/guardar")
    public String crearFinca(@RequestParam String nombre,
                             @RequestParam EstadoFinca estado,
                             @RequestParam(required = false) String ciudad,
                             @RequestParam(required = false) String provincia,
                             @RequestParam(required = false) Double area) {

        Usuario usuario = usuarioService.obtenerUsuarioEnSesion()
                .orElseThrow(() -> new IllegalStateException("No hay usuario en sesión"));

        if (usuario.getRol() != Rol.GERENTE) {
            return "redirect:/home";
        }

        Gerente gerente = gerenteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new IllegalStateException("Gerente no encontrado"));

        Finca finca = new Finca();
        finca.setNombre(nombre.trim());
        finca.setEstado(estado);
        finca.setGerente(gerente);

        if (ciudad != null && !ciudad.trim().isEmpty()) {
            finca.setCiudad(ciudad.trim());
        }
        if (provincia != null && !provincia.trim().isEmpty()) {
            finca.setProvincia(provincia.trim());
        }
        finca.setArea(area); // puede ser null

        fincaService.guardar(finca);

        return "redirect:/fincas/";
    }

    // ================== FORMULARIO EDITAR ==================
    // ================== FORMULARIO EDITAR ==================
// ================== FORMULARIO EDITAR ==================
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {

        Finca finca = fincaService.encontrarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        model.addAttribute("finca", finca);
        model.addAttribute("estados", Arrays.asList(EstadoFinca.values()));

        // lista de gerentes para el <select>
        model.addAttribute("gerentes", gerenteRepository.findAll());

        // ---- gerente en sesión para el navbar ----
        usuarioService.obtenerUsuarioEnSesion().ifPresent(usuario -> {
            if (usuario.getRol() == Rol.GERENTE) {
                gerenteRepository.findByUsuarioId(usuario.getId())
                        .ifPresent(g -> model.addAttribute("gerente", g));
            }
        });

        return "fincaForm"; // templates/fincaForm.html
    }



    // ================== POST EDITAR ==================
// ================== POST EDITAR ==================
    @PostMapping("/{id}")
    public String actualizarFinca(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("gerenteId") Long gerenteId,
            @RequestParam("estado") EstadoFinca estado,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) Double area) {

        Finca finca = fincaService.encontrarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        // actualizar nombre
        finca.setNombre(nombre != null ? nombre.trim() : null);

        // actualizar gerente
        Gerente nuevoGerente = gerenteRepository.findById(gerenteId)
                .orElseThrow(() -> new IllegalArgumentException("Gerente no encontrado"));
        finca.setGerente(nuevoGerente);

        // actualizar estado
        finca.setEstado(estado);

        // ciudad
        if (ciudad != null && !ciudad.trim().isEmpty()) {
            finca.setCiudad(ciudad.trim());
        } else {
            finca.setCiudad(null);
        }

        // provincia
        if (provincia != null && !provincia.trim().isEmpty()) {
            finca.setProvincia(provincia.trim());
        } else {
            finca.setProvincia(null);
        }

        // área (puede ser null)
        finca.setArea(area);

        fincaService.guardar(finca);

        return "redirect:/fincas/";
    }


    // ================== ELIMINAR FINCA ==================
    @PostMapping("/{id}/eliminar")
    public String eliminarFinca(@PathVariable Long id) {

        fincaService.eliminarPorId(id); // ya hace el borrado en cascada manual
        return "redirect:/fincas/";
    }




}
