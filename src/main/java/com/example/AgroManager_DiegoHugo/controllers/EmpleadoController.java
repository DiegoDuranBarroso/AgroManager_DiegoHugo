package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Gerente;
import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
import com.example.AgroManager_DiegoHugo.data.repositories.GerenteRepository;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
import com.example.AgroManager_DiegoHugo.data.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final UsuarioService usuarioService;
    private final GerenteRepository gerenteRepository;

    @Autowired
    public EmpleadoController(EmpleadoService empleadoService,
                              UsuarioService usuarioService,
                              GerenteRepository gerenteRepository) {
        this.empleadoService = empleadoService;
        this.usuarioService = usuarioService;
        this.gerenteRepository = gerenteRepository;
    }

    @GetMapping("/")
    public String listarEmpleados(@RequestParam(name = "nombre", required = false) String nombre,
                                  Model model) {

        if (nombre != null && !nombre.trim().isEmpty()) {
            model.addAttribute("empleados", empleadoService.buscarPorNombre(nombre.trim()));
            model.addAttribute("filtroNombre", nombre.trim());
        } else {
            model.addAttribute("empleados", empleadoService.encontrarTodos());
        }

        // Añadir gerente al modelo si está logueado
        usuarioService.obtenerUsuarioEnSesion().ifPresent(usuario -> {
            if (usuario.getRol() == Rol.GERENTE) {
                gerenteRepository.findByUsuarioId(usuario.getId())
                        .ifPresent(g -> model.addAttribute("gerente", g));
            }
        });

        return "empleados";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {

        model.addAttribute("empleado", new Empleado());
        model.addAttribute("usuarios", usuarioService.encontrarTodos());

        // Añadir gerente al modelo si corresponde
        usuarioService.obtenerUsuarioEnSesion().ifPresent(usuario -> {
            if (usuario.getRol() == Rol.GERENTE) {
                gerenteRepository.findByUsuarioId(usuario.getId())
                        .ifPresent(g -> model.addAttribute("gerente", g));
            }
        });

        return "empleadoForm";
    }

    @PostMapping("/")
    public String crearEmpleado(@ModelAttribute("empleado") Empleado empleado,
                                @RequestParam Long usuarioId) {

        Usuario usuario = usuarioService.encontrarPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        empleado.setUsuario(usuario);
        empleadoService.guardar(empleado);

        return "redirect:/empleados/";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {

        Optional<Empleado> opt = empleadoService.encontrarPorId(id);

        if (opt.isPresent()) {
            model.addAttribute("empleado", opt.get());
            model.addAttribute("usuarios", usuarioService.encontrarTodos());

            // Añadir gerente al modelo
            usuarioService.obtenerUsuarioEnSesion().ifPresent(usuario -> {
                if (usuario.getRol() == Rol.GERENTE) {
                    gerenteRepository.findByUsuarioId(usuario.getId())
                            .ifPresent(g -> model.addAttribute("gerente", g));
                }
            });

            return "empleadoForm";
        }

        return "redirect:/empleados/";
    }

    @PostMapping("/{id}")
    public String actualizarEmpleado(@PathVariable Long id,
                                     @ModelAttribute("empleado") Empleado empleado,
                                     @RequestParam Long usuarioId) {

        empleado.setId(id);

        Usuario usuario = usuarioService.encontrarPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        empleado.setUsuario(usuario);
        empleadoService.guardar(empleado);

        return "redirect:/empleados/";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarEmpleado(@PathVariable Long id) {
        empleadoService.eliminarPorId(id);
        return "redirect:/empleados/";
    }
}
