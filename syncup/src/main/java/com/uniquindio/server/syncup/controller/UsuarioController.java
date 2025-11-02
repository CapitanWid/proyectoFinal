package com.uniquindio.server.syncup.controller;


import com.uniquindio.server.syncup.datastructures.TablaHashUsuarios;
import com.uniquindio.server.syncup.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    // 🔹 Accede a la misma tabla en memoria del FormularioController
    private static final TablaHashUsuarios tablaUsuarios = FormularioController.getTablaUsuarios();

    // ============================================================
    // LISTAR TODOS LOS USUARIOS
    // ============================================================
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = tablaUsuarios.obtenerTodos();
        if (usuarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(usuarios);
    }

    // ============================================================
    // ELIMINAR USUARIO POR SU NOMBRE DE USUARIO
    // ============================================================
    @DeleteMapping("/{usuario}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable String usuario) {
        Usuario usuarioEncontrado = tablaUsuarios.buscarUsuario(usuario);

        if (usuarioEncontrado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        tablaUsuarios.eliminarUsuario(usuario);
        return ResponseEntity.ok("Usuario eliminado exitosamente");
    }

}

