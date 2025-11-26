package com.example.AgroManager_DiegoHugo.data.services;

import com.example.AgroManager_DiegoHugo.data.model.Rol;
import com.example.AgroManager_DiegoHugo.data.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<Usuario> encontrarTodos();

    List<Usuario> encontrarActivos();

    Optional<Usuario> encontrarPorId(Long id);

    Optional<Usuario> encontrarPorUsername(String username);

    List<Usuario> encontrarPorRol(Rol rol);

    Usuario guardar(Usuario usuario);

    void desactivar(Long id);

    void eliminarPorId(Long id);
}
