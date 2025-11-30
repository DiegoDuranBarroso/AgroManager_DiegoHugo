package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;
import com.example.AgroManager_DiegoHugo.data.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> encontrarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> encontrarActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> encontrarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido: " + id);
        }
        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> encontrarPorUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username no puede estar vacío");
        }
        return usuarioRepository.findByUsername(username.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> encontrarPorRol(Rol rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        return usuarioRepository.findByRol(rol);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El username no puede estar vacío");
        }
        if (usuario.getPasswordHash() == null || usuario.getPasswordHash().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        if (usuario.getRol() == null) {
            throw new IllegalArgumentException("El rol del usuario es obligatorio");
        }

        // Podrías añadir aquí validación de unicidad de username si quieres
        return usuarioRepository.save(usuario);
    }

    @Override
    public void desactivar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario con ID " + id + " no encontrado"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    public void eliminarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido: " + id);
        }
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario con ID " + id + " no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> validarLogin(String username, String password) {

        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return Optional.empty();
        }

        Optional<Usuario> opt = usuarioRepository.findByUsername(username);
        if (opt.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = opt.get();

        String stored = usuario.getPasswordHash();

        if (stored == null || !stored.startsWith("{noop}")) {
            return Optional.empty();
        }

        String storedPlain = stored.substring(6);

        if (!storedPlain.equals(password)) {
            return Optional.empty();
        }

        return Optional.of(usuario);
    }

    public Optional<Usuario> obtenerUsuarioEnSesion() {
        Long id = (Long) ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes())
                .getRequest()
                .getSession()
                .getAttribute("usuarioId");

        if (id == null) return Optional.empty();

        return encontrarPorId(id);
    }



}
