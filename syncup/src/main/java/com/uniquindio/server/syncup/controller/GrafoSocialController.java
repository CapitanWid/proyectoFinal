package com.uniquindio.server.syncup.controller;

import com.uniquindio.server.syncup.datastructures.GrafoSocial;
import com.uniquindio.server.syncup.datastructures.TablaHashUsuarios;
import com.uniquindio.server.syncup.datastructures.ListaSimple;
import com.uniquindio.server.syncup.model.Usuario;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api/grafo")
@CrossOrigin(origins = "*")
public class GrafoSocialController {

    private final GrafoSocial grafo = new GrafoSocial();

    // Tabla de usuarios compartida
    private final TablaHashUsuarios tablaUsuarios = FormularioController.getTablaUsuarios();

    // =========================================================
    //  SINCRONIZAR USUARIOS EXISTENTES CON EL GRAFO
    // =========================================================
    @PostMapping("/sincronizar")
    public String sincronizarUsuarios() {
        if (tablaUsuarios == null) {
            System.err.println("⚠️ La tabla de usuarios aún no está inicializada.");
            return "Error: la tabla de usuarios no está disponible.";
        }

        // Lista propia
        ListaSimple<Usuario> lista = tablaUsuarios.obtenerTodos();
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
    //  SINCRONIZACIÓN AUTOMÁTICA ANTES DE CADA REQUEST
    // =========================================================
    @ModelAttribute
    public void actualizarAntesDeCualquierPeticion() {
        try {
            sincronizarUsuarios();
        } catch (Exception e) {
            System.err.println("⚠️ Error durante la sincronización automática: " + e.getMessage());
        }
    }

    // =========================================================
    //  OBTENER TODOS LOS USUARIOS DEL GRAFO (SIN LIST)
    // =========================================================
    @GetMapping("/usuarios")
    public Usuario[] listarUsuarios() {
        // El grafo debe tener un método getUsuarios() → ListaSimple<Usuario>
        ListaSimple<Usuario> lista = grafo.getUsuarios();
        return lista.toArray(Usuario.class);
    }

    // =========================================================
    //  RELACIONES: SEGUIR / DEJAR DE SEGUIR
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
    //  BFS: AMIGOS DE AMIGOS (SIN LIST)
    // =========================================================
    @GetMapping("/amigos")
    public Usuario[] obtenerAmigos(@RequestParam String usuario, @RequestParam int nivel) {
        ListaSimple<Usuario> lista = grafo.bfsAmigos(usuario, nivel);
        return lista.toArray(Usuario.class);
    }

    // =========================================================
    //  INICIALIZAR GRAFO AL ARRANCAR EL SERVIDOR
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

    // =========================================================
    //  LISTA DE AMIGOS SIGUIENDO
    // =========================================================
    @GetMapping("/siguiendo")
    public Usuario[] obtenerSiguiendo(@RequestParam String usuario) {

        ListaSimple<Usuario> lista = grafo.obtenerSeguidos(usuario);

        return lista.toArray(Usuario.class);
    }

}
