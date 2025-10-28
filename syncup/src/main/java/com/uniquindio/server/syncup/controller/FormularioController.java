package com.uniquindio.server.syncup.controller;

import com.uniquindio.server.syncup.datastructures.TablaHashUsuarios;
import com.uniquindio.server.syncup.model.Formulario;
import com.uniquindio.server.syncup.model.Usuario;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/api/formulario")
public class FormularioController {

    private static final String FILE_PATH = "D:\\datos_syncup\\users.csv";

    // 🔹 Estructura de datos en memoria
    private static final TablaHashUsuarios tablaUsuarios = new TablaHashUsuarios(100);

    // =========================================================
    // CONSTRUCTOR: carga inicial desde CSV
    // =========================================================
    public FormularioController() {
        cargarUsuariosDesdeCSV(); // ✅ Se ejecuta al iniciar el servidor
    }

    // =========================================================
    // REGISTRO DE NUEVO USUARIO (solo RAM)
    // =========================================================
    @PostMapping
    public String registrarUsuario(@RequestBody Formulario formulario) {
        try {
            Usuario nuevoUsuario = new Usuario(
                    formulario.getUsuario(),
                    formulario.getNombre(),
                    formulario.getApellido(),
                    formulario.getCorreo(),
                    formulario.getContrasena()
            );

            if (tablaUsuarios.existeUsuario(nuevoUsuario.getUsuario())) {
                return "Usuario ya existe";
            }

            // Guardar solo en memoria
            tablaUsuarios.agregarUsuario(nuevoUsuario);

            System.out.println("Usuario registrado en RAM: " + nuevoUsuario);
            return "Formulario recibido y usuario registrado";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al procesar el registro";
        }
    }

    // =========================================================
    // ACTUALIZAR DATOS DEL USUARIO (solo RAM)
    // =========================================================
    @PutMapping("/actualizar/{usuario}")
    public String actualizarUsuario(@PathVariable String usuario, @RequestBody Formulario datosActualizados) {
        Usuario existente = tablaUsuarios.buscarUsuario(usuario);

        if (existente == null) {
            return "Usuario no encontrado";
        }

        existente.setNombre(datosActualizados.getNombre());
        existente.setApellido(datosActualizados.getApellido());
        existente.setCorreo(datosActualizados.getCorreo());

        // Agregar soporte para actualizar la contraseña también
        if (datosActualizados.getContrasena() != null && !datosActualizados.getContrasena().isEmpty()) {
            existente.setContrasena(datosActualizados.getContrasena());
        }

        System.out.println("✅ Usuario actualizado en RAM: " + existente);
        return "Usuario actualizado correctamente";
    }



    // =========================================================
    // MÉTODOS AUXILIARES
    // =========================================================

    private void cargarUsuariosDesdeCSV() {
        Path path = Paths.get(FILE_PATH);
        if (!Files.exists(path)) {
            System.out.println("⚠ No se encontró el archivo CSV en: " + FILE_PATH);
            return;
        }

        try {
            List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);
            int count = 0;

            for (String linea : lineas) {
                String[] partes = linea.split(",");
                if (partes.length == 5) {
                    Usuario usuario = new Usuario(
                            partes[0].trim(), // usuario
                            partes[1].trim(), // nombre
                            partes[2].trim(), // apellido
                            partes[3].trim(), // correo
                            partes[4].trim()  // contraseña
                    );

                    if (!tablaUsuarios.existeUsuario(usuario.getUsuario())) {
                        tablaUsuarios.agregarUsuario(usuario);
                        count++;
                    }
                }
            }
            System.out.println("✅ Usuarios cargados desde CSV: " + count);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error al leer el archivo CSV");
        }
    }

    // Permite a otros controladores acceder a la tabla
    public static TablaHashUsuarios getTablaUsuarios() {
        return tablaUsuarios;
    }
}
