package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
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

    @Autowired
    public EmpleadoController(EmpleadoService empleadoService,
                              UsuarioService usuarioService) {
        this.empleadoService = empleadoService;
        this.usuarioService = usuarioService;
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
        return "empleados"; // templates/empleados.html
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("empleado", new Empleado());
        // Lista de usuarios para asociar al empleado
        model.addAttribute("usuarios", usuarioService.encontrarTodos());
        return "empleadoForm"; // templates/empleadoForm.html
    }

    @PostMapping("/")
    public String crearEmpleado(@ModelAttribute("empleado") Empleado empleado,
                                @RequestParam Long usuarioId) {

        // Asociar el usuario al empleado antes de guardar
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
            // Volvemos a pasar la lista de usuarios para el combo
            model.addAttribute("usuarios", usuarioService.encontrarTodos());
            return "empleadoForm";
        } else {
            return "redirect:/empleados/";
        }
    }

    @PostMapping("/{id}")
    public String actualizarEmpleado(@PathVariable Long id,
                                     @ModelAttribute("empleado") Empleado empleado,
                                     @RequestParam Long usuarioId) {

        empleado.setId(id); // requiere setId en Empleado

        // Igual que en crear: cargar y asociar usuario
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
