package com.uniquindio.server.syncup.controller;

import com.uniquindio.server.syncup.datastructures.TablaHashUsuarios;
import com.uniquindio.server.syncup.model.Formulario;
import com.uniquindio.server.syncup.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    // Accede a la tabla cargada desde el FormularioController
    private static final TablaHashUsuarios tablaUsuarios = FormularioController.getTablaUsuarios();

    @PostMapping
    public ResponseEntity<?> login(@RequestBody Formulario loginRequest) {
        // Buscar usuario directamente en memoria
        Usuario usuario = tablaUsuarios.buscarUsuario(loginRequest.getUsuario());

        if (usuario == null || !usuario.getContrasena().equals(loginRequest.getContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario o contrasena incorrectos");
        }

        // ✅ Si las credenciales son válidas, devolver el usuario como JSON
        return ResponseEntity.ok(usuario);
    }
}
