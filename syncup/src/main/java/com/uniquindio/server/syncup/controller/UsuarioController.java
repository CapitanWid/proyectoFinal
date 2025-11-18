package com.uniquindio.server.syncup.controller;


import com.uniquindio.server.syncup.datastructures.ListaSimple;
import com.uniquindio.server.syncup.datastructures.TablaHashUsuarios;
import com.uniquindio.server.syncup.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    //  Accede a la misma tabla en memoria del FormularioController
    private static final TablaHashUsuarios tablaUsuarios = FormularioController.getTablaUsuarios();

    // ============================================================
    // LISTAR TODOS LOS USUARIOS
    // ============================================================


    @GetMapping
    public ResponseEntity<Usuario[]> listarUsuarios() {

        // 1. Obtener la lista propia
        ListaSimple<Usuario> lista = tablaUsuarios.obtenerTodos();

        // 2. Si está vacía → 204
        if (lista.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new Usuario[0]);
        }

        // 3. Convertir a arreglo JSON-friendly
        Usuario[] arr = lista.toArray(Usuario.class);

        // 4. Retornar arreglo
        return ResponseEntity.ok(arr);
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

