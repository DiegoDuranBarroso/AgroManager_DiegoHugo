package com.example.AgroManager_DiegoHugo.controllers;

import com.example.AgroManager_DiegoHugo.data.model.Empleado;
import com.example.AgroManager_DiegoHugo.data.model.Gerente;
import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
import com.example.AgroManager_DiegoHugo.data.repositories.EmpleadoRepository;
import com.example.AgroManager_DiegoHugo.data.repositories.GerenteRepository;
import com.example.AgroManager_DiegoHugo.data.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/usuario")
public class UsuarioPerfilController {

    private final UsuarioService usuarioService;
    private final GerenteRepository gerenteRepository;
    private final EmpleadoRepository empleadoRepository;

    @Autowired
    public UsuarioPerfilController(UsuarioService usuarioService,
                                   GerenteRepository gerenteRepository,
                                   EmpleadoRepository empleadoRepository) {
        this.usuarioService = usuarioService;
        this.gerenteRepository = gerenteRepository;
        this.empleadoRepository = empleadoRepository;
    }

    // ================= VER PERFIL =================
    @GetMapping("/perfil")
    public String verPerfil(Model model) {

        Usuario usuario = usuarioService.obtenerUsuarioEnSesion()
                .orElseThrow(() -> new IllegalStateException("No hay usuario en sesión"));

        // Nombre bonito según sea gerente o empleado
        String nombreCompleto = usuario.getUsername();
        if (usuario.getRol() == Rol.GERENTE) {
            Gerente g = gerenteRepository.findByUsuarioId(usuario.getId())
                    .orElse(null);
            if (g != null) {
                nombreCompleto = g.getNombre();
                model.addAttribute("gerente", g);
            }
        } else if (usuario.getRol() == Rol.EMPLEADO) {
            Empleado e = empleadoRepository.findByUsuarioId(usuario.getId())
                    .orElse(null);
            if (e != null) {
                nombreCompleto = e.getNombre();
                model.addAttribute("empleado", e);
            }
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("nombreCompleto", nombreCompleto);

        return "usuarioPerfil"; // templates/usuarioPerfil.html
    }

    // ================= ACTUALIZAR PERFIL =================
    @PostMapping("/perfil")
    public String actualizarPerfil(@RequestParam String username,
                                   @RequestParam(required = false) String nuevaPassword,
                                   @RequestParam("foto") MultipartFile foto,
                                   RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioService.obtenerUsuarioEnSesion()
                .orElseThrow(() -> new IllegalStateException("No hay usuario en sesión"));

        // Actualizar username
        if (username != null && !username.trim().isEmpty()) {
            usuario.setUsername(username.trim());
        }

        // Actualizar password (simple, sin encriptar, como tus inserts {noop})
        if (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
            usuario.setPasswordHash("{noop}" + nuevaPassword.trim());
        }

        // Guardar foto si se ha subido
        if (foto != null && !foto.isEmpty()) {
            try {
                // Carpeta "uploads" en el directorio de ejecución
                Path uploadsDir = Paths.get("uploads");
                if (!Files.exists(uploadsDir)) {
                    Files.createDirectories(uploadsDir);
                }

                String originalFilename = StringUtils.cleanPath(foto.getOriginalFilename());
                String extension = "";

                int dotIndex = originalFilename.lastIndexOf('.');
                if (dotIndex >= 0) {
                    extension = originalFilename.substring(dotIndex);
                }

                // Nombre de archivo: user_{id}.ext
                String nombreArchivo = "user_" + usuario.getId() + extension;

                Path destino = uploadsDir.resolve(nombreArchivo);
                Files.copy(foto.getInputStream(), destino, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                usuario.setFotoPerfil(nombreArchivo);

            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("errorPerfil", "No se pudo guardar la imagen de perfil.");
                return "redirect:/usuario/perfil";
            }
        }

        usuarioService.guardar(usuario);

        redirectAttributes.addFlashAttribute("perfilGuardado", true);
        return "redirect:/usuario/perfil";
    }
}
