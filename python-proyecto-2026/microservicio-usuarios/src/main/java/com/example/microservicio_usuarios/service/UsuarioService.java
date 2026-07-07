package com.example.microservicio_usuarios.service;

import com.example.microservicio_usuarios.model.Usuario;
import com.example.microservicio_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean existeUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> actualizarUsuario(Integer id, Usuario usuarioActualizado) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    usuarioExistente.setUsername(usuarioActualizado.getUsername());
                    usuarioExistente.setPassword(usuarioActualizado.getPassword());
                    usuarioExistente.setEmail(usuarioActualizado.getEmail());
                    usuarioExistente.setRol(usuarioActualizado.getRol());

                    return usuarioRepository.save(usuarioExistente);
                });
    }

    public boolean eliminarUsuario(Integer id) {
    if (!usuarioRepository.existsById(id)) {
        return false;
    }

    usuarioRepository.deleteById(id);
    return true;
}
}