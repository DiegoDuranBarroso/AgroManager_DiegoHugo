package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.EstadoFinca;
import com.example.AgroManager_DiegoHugo.data.model.Finca;
import com.example.AgroManager_DiegoHugo.data.model.Gerente;
import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
import com.example.AgroManager_DiegoHugo.data.repositories.GerenteRepository;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
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
                           GerenteRepository gerenteRepository,
                           EmpleadoService empleadoService) {
        this.fincaService = fincaService;
        this.usuarioService = usuarioService;
        this.gerenteRepository = gerenteRepository;
    }

    // ================== LISTAR FINCAS ==================
    @GetMapping("/")
    public String listarFincas(Model model) {

        model.addAttribute("fincas", fincaService.encontrarTodas());

        usuarioService.obtenerUsuarioEnSesion().ifPresent(usuario -> {
            if (usuario.getRol() == Rol.GERENTE) {
                gerenteRepository.findByUsuarioId(usuario.getId())
                        .ifPresent(g -> model.addAttribute("gerente", g));
            }
        });

        return "fincas";
    }

    // ================== NUEVA FINCA (GET) ==================
    @GetMapping("/nueva")
    public String mostrarFormularioNuevaFinca(Model model) {

        Usuario usuario = usuarioService.obtenerUsuarioEnSesion()
                .orElseThrow(() -> new IllegalStateException("No hay usuario en sesión"));

        if (usuario.getRol() != Rol.GERENTE) {
            return "redirect:/home";
        }

        Gerente gerente = gerenteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new IllegalStateException("Gerente no encontrado"));

        model.addAttribute("finca", new Finca());
        model.addAttribute("estados", Arrays.asList(EstadoFinca.values()));
        model.addAttribute("gerente", gerente);

        return "fincaNueva";
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
        finca.setArea(area);

        fincaService.guardar(finca);

        return "redirect:/fincas/";
    }

    // ================== FORMULARIO EDITAR (GET) ==================
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {

        Finca finca = fincaService.encontrarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        model.addAttribute("finca", finca);
        model.addAttribute("estados", Arrays.asList(EstadoFinca.values()));
        model.addAttribute("gerentes", gerenteRepository.findAll());

        usuarioService.obtenerUsuarioEnSesion().ifPresent(usuario -> {
            if (usuario.getRol() == Rol.GERENTE) {
                gerenteRepository.findByUsuarioId(usuario.getId())
                        .ifPresent(g -> model.addAttribute("gerente", g));
            }
        });

        return "fincaForm";
    }

    // ================== POST EDITAR ==================
    @PostMapping("/{id}")
    public String actualizarFinca(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("gerenteId") Long gerenteId,
            @RequestParam("estado") EstadoFinca estado,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) Double area,
            @RequestParam(required = false) Double latitud,
            @RequestParam(required = false) Double longitud
    ) {

        Finca finca = fincaService.encontrarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        finca.setNombre(nombre != null ? nombre.trim() : null);

        Gerente nuevoGerente = gerenteRepository.findById(gerenteId)
                .orElseThrow(() -> new IllegalArgumentException("Gerente no encontrado"));
        finca.setGerente(nuevoGerente);

        finca.setEstado(estado);

        finca.setCiudad((ciudad != null && !ciudad.trim().isEmpty()) ? ciudad.trim() : null);
        finca.setProvincia((provincia != null && !provincia.trim().isEmpty()) ? provincia.trim() : null);
        finca.setArea(area);

        finca.setLatitud(latitud);
        finca.setLongitud(longitud);

        fincaService.guardar(finca);

        // 👉 redirige al detalle con el patrón correcto
        return "redirect:/fincas/" + id + "/detalle";
    }

    // ================== ELIMINAR FINCA ==================
    @PostMapping("/{id}/eliminar")
    public String eliminarFinca(@PathVariable Long id) {

        fincaService.eliminarPorId(id);
        return "redirect:/fincas/";
    }

    // ================== DETALLE FINCA ==================
    @GetMapping("/{id}/detalle")
    public String verDetalleFinca(@PathVariable Long id, Model model) {

        Finca finca = fincaService.encontrarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Finca no encontrada"));

        model.addAttribute("finca", finca);

        usuarioService.obtenerUsuarioEnSesion().ifPresent(usuario -> {
            if (usuario.getRol() == Rol.GERENTE) {
                gerenteRepository.findByUsuarioId(usuario.getId())
                        .ifPresent(g -> model.addAttribute("gerente", g));
            }
        });

        // 👉 aquí se devuelve la vista, NO un redirect
        return "fincaDetalle";
    }
}
