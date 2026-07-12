package com.example.microservicio_usuarios.service;

import com.example.microservicio_usuarios.exception.CredencialesInvalidasException;
import com.example.microservicio_usuarios.exception.ReglaNegocioException;
import com.example.microservicio_usuarios.model.Usuario;
import com.example.microservicio_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Debe listar usuarios")
    void shouldListarUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(crearUsuario(1), crearUsuario(2)));

        List<Usuario> resultado = usuarioService.listarUsuarios();

        assertEquals(2, resultado.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener usuario por ID")
    void shouldObtenerUsuarioPorId() {
        Usuario usuario = crearUsuario(1);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals(usuario.getEmail(), resultado.get().getEmail());
    }

    @Test
    @DisplayName("Debe buscar y verificar un usuario por email")
    void shouldBuscarYVerificarPorEmail() {
        Usuario usuario = crearUsuario(1);
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        assertTrue(usuarioService.buscarPorEmail(usuario.getEmail()).isPresent());
        assertTrue(usuarioService.existeUsuarioPorEmail(usuario.getEmail()));
        verify(usuarioRepository, times(2)).findByEmail(usuario.getEmail());
    }

    @Test
    @DisplayName("Debe guardar un usuario cuando sus datos son únicos")
    void shouldGuardarUsuario() {
        Usuario usuario = crearUsuario(1);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioService.guardarUsuario(usuario);

        assertNotNull(resultado);
        assertEquals(usuario.getUsername(), resultado.getUsername());
        verify(usuarioRepository).existsByUsername(usuario.getUsername());
        verify(usuarioRepository).existsByEmail(usuario.getEmail());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Debe rechazar un username duplicado")
    void shouldRejectUsernameDuplicado() {
        Usuario usuario = crearUsuario(1);
        when(usuarioRepository.existsByUsername(usuario.getUsername())).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> usuarioService.guardarUsuario(usuario));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe rechazar un email duplicado")
    void shouldRejectEmailDuplicado() {
        Usuario usuario = crearUsuario(1);
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> usuarioService.guardarUsuario(usuario));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe actualizar un usuario existente")
    void shouldActualizarUsuario() {
        Usuario usuarioExistente = crearUsuario(1);
        Usuario usuarioActualizado = crearUsuario(1);
        usuarioActualizado.setUsername("usuario_actualizado");
        usuarioActualizado.setEmail("actualizado@saludconecta.cl");
        usuarioActualizado.setRol("ADMIN");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Usuario> resultado = usuarioService.actualizarUsuario(1, usuarioActualizado);

        assertTrue(resultado.isPresent());
        assertEquals("usuario_actualizado", resultado.get().getUsername());
        assertEquals("actualizado@saludconecta.cl", resultado.get().getEmail());
        assertEquals("ADMIN", resultado.get().getRol());
    }

    @Test
    @DisplayName("Debe retornar vacío al actualizar un usuario inexistente")
    void shouldReturnEmptyWhenActualizarUsuarioNoExiste() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.actualizarUsuario(99, crearUsuario(99));

        assertTrue(resultado.isEmpty());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe eliminar un usuario existente")
    void shouldEliminarUsuario() {
        when(usuarioRepository.existsById(1)).thenReturn(true);

        assertTrue(usuarioService.eliminarUsuario(1));
        verify(usuarioRepository).deleteById(1);
    }

    @Test
    @DisplayName("Debe retornar false al eliminar un usuario inexistente")
    void shouldReturnFalseWhenEliminarUsuarioNoExiste() {
        when(usuarioRepository.existsById(99)).thenReturn(false);

        assertFalse(usuarioService.eliminarUsuario(99));
        verify(usuarioRepository, never()).deleteById(99);
    }

    @Test
    @DisplayName("Debe autenticar un usuario con credenciales correctas")
    void shouldAutenticarUsuario() {
        Usuario usuario = crearUsuario(1);
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.autenticarUsuario(usuario.getEmail(), "123456");

        assertEquals(usuario.getId(), resultado.getId());
    }

    @Test
    @DisplayName("Debe rechazar un email no registrado")
    void shouldRejectEmailNoRegistrado() {
        when(usuarioRepository.findByEmail("noexiste@correo.cl")).thenReturn(Optional.empty());

        assertThrows(
                CredencialesInvalidasException.class,
                () -> usuarioService.autenticarUsuario("noexiste@correo.cl", "123456")
        );
    }

    @Test
    @DisplayName("Debe rechazar una contraseña incorrecta")
    void shouldRejectPasswordIncorrecta() {
        Usuario usuario = crearUsuario(1);
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        assertThrows(
                CredencialesInvalidasException.class,
                () -> usuarioService.autenticarUsuario(usuario.getEmail(), "incorrecta")
        );
    }

    private Usuario crearUsuario(Integer id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUsername("usuario" + id);
        usuario.setPassword("123456");
        usuario.setEmail("usuario" + id + "@saludconecta.cl");
        usuario.setRol("USER");
        return usuario;
    }
}
