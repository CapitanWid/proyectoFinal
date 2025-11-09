package com.uniquindio.server.syncup.controller;

import com.uniquindio.server.syncup.datastructures.GrafoSocial;
import com.uniquindio.server.syncup.datastructures.TablaHashUsuarios;
import com.uniquindio.server.syncup.model.Usuario;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/grafo")
@CrossOrigin(origins = "*")
public class GrafoSocialController {

    private final GrafoSocial grafo = new GrafoSocial();

    // ⚙️ Acceso a la tabla compartida de usuarios
    private final TablaHashUsuarios tablaUsuarios = FormularioController.getTablaUsuarios();

    // =========================================================
    // 🔹 SINCRONIZAR USUARIOS EXISTENTES CON EL GRAFO
    // =========================================================
    @PostMapping("/sincronizar")
    public String sincronizarUsuarios() {
        if (tablaUsuarios == null) {
            System.err.println("⚠️ La tabla de usuarios aún no está inicializada.");
            return "Error: la tabla de usuarios no está disponible.";
        }

        LinkedList<Usuario> lista = tablaUsuarios.obtenerTodos();
        int nuevos = 0;

        for (Usuario u : lista) {
            if (grafo.buscarNodo(u.getUsuario()) == null) {
                grafo.agregarUsuario(u);
                nuevos++;
            }
        }

        System.out.println("🔄 Grafo sincronizado. Usuarios nuevos agregados: " + nuevos);
        return "Usuarios sincronizados en el grafo: " + grafo.getUsuarios().size();
    }

    // =========================================================
    // 🔹 SINCRONIZACIÓN AUTOMÁTICA ANTES DE CADA PETICIÓN
    // =========================================================
    @ModelAttribute
    public void actualizarAntesDeCualquierPeticion() {
        try {
            sincronizarUsuarios(); // 🔄 Se ejecuta automáticamente antes de cualquier request
        } catch (Exception e) {
            System.err.println("⚠️ Error durante la sincronización automática: " + e.getMessage());
        }
    }

    // =========================================================
    // 🔹 OBTENER TODOS LOS USUARIOS DEL GRAFO
    // =========================================================
    @GetMapping("/usuarios")
    public List<Usuario> listarUsuarios() {
        return grafo.getUsuarios();
    }

    // =========================================================
    // 🔹 RELACIONES: SEGUIR / DEJAR DE SEGUIR
    // =========================================================
    @PostMapping("/seguir")
    public String seguir(@RequestParam String usuario1, @RequestParam String usuario2) {
        grafo.seguir(usuario1, usuario2);
        return usuario1 + " ahora sigue a " + usuario2;
    }

    @PostMapping("/dejarSeguir")
    public String dejarSeguir(@RequestParam String usuario1, @RequestParam String usuario2) {
        grafo.dejarDeSeguir(usuario1, usuario2);
        return usuario1 + " dejó de seguir a " + usuario2;
    }

    // =========================================================
    // 🔹 BFS: AMIGOS DE AMIGOS
    // =========================================================
    @GetMapping("/amigos")
    public List<Usuario> obtenerAmigos(@RequestParam String usuario, @RequestParam int nivel) {
        return grafo.bfsAmigos(usuario, nivel).toList();
    }

    // =========================================================
    // 🔹 SINCRONIZACIÓN AUTOMÁTICA AL INICIO DEL SERVIDOR
    // =========================================================
    @PostConstruct
    public void inicializarGrafo() {
        try {
            System.out.println("🚀 Inicializando Grafo Social al iniciar el servidor...");
            sincronizarUsuarios();
            System.out.println("✅ Grafo inicializado con " + grafo.getUsuarios().size() + " usuarios.");
        } catch (Exception e) {
            System.err.println("⚠️ Error al inicializar el grafo social: " + e.getMessage());
        }
    }
}
