package com.uniquindio.server.syncup.controller;

import com.uniquindio.server.syncup.datastructures.TablaHashUsuarios;
import com.uniquindio.server.syncup.model.Cancion;
import com.uniquindio.server.syncup.model.Usuario;
import com.uniquindio.server.syncup.service.RepositorioCanciones;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritosController {

    private final TablaHashUsuarios tablaUsuarios;
    private final RepositorioCanciones repoCanciones; // 🔹 Acceso real al repositorio

    public FavoritosController() {
        this.tablaUsuarios = FormularioController.getTablaUsuarios();
        this.repoCanciones = CancionController.getRepositorio(); 

    }

    @PostMapping("/agregar")
    public String agregarFavorito(@RequestParam String usuarioId, @RequestParam String cancionId) {
        Usuario usuario = tablaUsuarios.buscarUsuario(usuarioId);
        if (usuario == null) {
            return "Usuario no encontrado";
        }

        Cancion cancion = buscarCancionPorId(cancionId);
        if (cancion == null) {
            return "Cancion no encontrada";
        }

        usuario.agregarFavorito(cancion);
        System.out.println("✅ Favorito agregado: " + cancion.getTitulo() + " para " + usuarioId);
        return "Cancion agregada a favoritos";
    }

    @DeleteMapping("/eliminar")
    public String eliminarFavorito(@RequestParam String usuarioId, @RequestParam String cancionId) {
        Usuario usuario = tablaUsuarios.buscarUsuario(usuarioId);
        if (usuario == null) {
            return "Usuario no encontrado";
        }

        Cancion cancion = buscarCancionPorId(cancionId);
        if (cancion == null) {
            return "Cancion no encontrada";
        }

        usuario.eliminarFavorito(cancion);
        System.out.println("🗑️ Favorito eliminado: " + cancion.getTitulo() + " para " + usuarioId);
        return "Cancion eliminada de favoritos";
    }

    @GetMapping("/{usuarioId}")
    public LinkedList<Cancion> obtenerFavoritos(@PathVariable String usuarioId) {
        Usuario usuario = tablaUsuarios.buscarUsuario(usuarioId);
        if (usuario == null) return new LinkedList<>();
        return usuario.getListaFavoritos();
    }

    private Cancion buscarCancionPorId(String id) {
        for (Cancion c : repoCanciones.getListaCanciones()) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }
}
