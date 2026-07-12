package com.example.microservicio_usuarios.service;

import com.example.microservicio_usuarios.exception.CredencialesInvalidasException;
import com.example.microservicio_usuarios.exception.ReglaNegocioException;
import com.example.microservicio_usuarios.model.Usuario;
import com.example.microservicio_usuarios.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarUsuarios() {
        log.info("Listando usuarios registrados");
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        log.info("Buscando usuario con id {}", id);
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean existeUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    public Usuario guardarUsuario(@Valid Usuario usuario) {
        validarDatosUnicos(usuario);
        log.info("Guardando usuario con email {}", usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> actualizarUsuario(Integer id, @Valid Usuario usuarioActualizado) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    validarDatosUnicosEnActualizacion(id, usuarioActualizado);
                    usuarioExistente.setUsername(usuarioActualizado.getUsername());
                    usuarioExistente.setPassword(usuarioActualizado.getPassword());
                    usuarioExistente.setEmail(usuarioActualizado.getEmail());
                    usuarioExistente.setRol(usuarioActualizado.getRol());

                    log.info("Actualizando usuario con id {}", id);
                    return usuarioRepository.save(usuarioExistente);
                });
    }

    public boolean eliminarUsuario(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            log.warn("Se intentó eliminar un usuario inexistente con id {}", id);
            return false;
        }

        log.info("Eliminando usuario con id {}", id);
        usuarioRepository.deleteById(id);
        return true;
    }

    public Usuario autenticarUsuario(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new CredencialesInvalidasException(
                        "El email ingresado no está registrado o no existe"
                ));

        if (!usuario.getPassword().equals(password)) {
            throw new CredencialesInvalidasException("La contraseña ingresada es incorrecta");
        }

        log.info("Credenciales validadas para el usuario {}", email);
        return usuario;
    }

    private void validarDatosUnicos(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new ReglaNegocioException("El username ingresado ya se encuentra registrado");
        }

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new ReglaNegocioException("El email ingresado ya se encuentra registrado");
        }
    }

    private void validarDatosUnicosEnActualizacion(Integer id, Usuario usuarioActualizado) {
        if (usuarioRepository.existsByUsernameAndIdNot(usuarioActualizado.getUsername(), id)) {
            throw new ReglaNegocioException("El username ingresado pertenece a otro usuario");
        }

        if (usuarioRepository.existsByEmailAndIdNot(usuarioActualizado.getEmail(), id)) {
            throw new ReglaNegocioException("El email ingresado pertenece a otro usuario");
        }
    }
}
