package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.services.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @Autowired
    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
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
        return "empleadoForm"; // templates/empleadoForm.html
    }

    @PostMapping("/")
    public String crearEmpleado(@ModelAttribute("empleado") Empleado empleado) {
        empleadoService.guardar(empleado);
        return "redirect:/empleados/";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Optional<Empleado> opt = empleadoService.encontrarPorId(id);
        if (opt.isPresent()) {
            model.addAttribute("empleado", opt.get());
            return "empleadoForm";
        } else {
            return "redirect:/empleados/";
        }
    }

    @PostMapping("/{id}")
    public String actualizarEmpleado(@PathVariable Long id,
                                     @ModelAttribute("empleado") Empleado empleado) {
        empleado.setId(id); // ⚠ requiere setId en Empleado
        empleadoService.guardar(empleado);
        return "redirect:/empleados/";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarEmpleado(@PathVariable Long id) {
        empleadoService.eliminarPorId(id);
        return "redirect:/empleados/";
    }
}
