package com.uniquindio.server.syncup.controller;

import com.uniquindio.server.syncup.model.Usuario;
import com.uniquindio.server.syncup.model.Cancion;
import com.uniquindio.server.syncup.datastructures.TablaHashUsuarios;
import com.uniquindio.server.syncup.service.RepositorioCanciones;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/metricas")
public class MetricasController {

    private final TablaHashUsuarios tablaUsuarios = FormularioController.getTablaUsuarios();
    private final RepositorioCanciones repo = CancionController.getRepositorio();

    // ============================================================
    // 1️⃣ Canciones más populares según favoritos
    // ============================================================
    @GetMapping("/populares")
    public List<Map<String, Object>> cancionesMasPopulares() {

        Map<String, Integer> conteo = new HashMap<>();

        // obtenerTodos() devuelve ListaSimple<Usuario>
        for (Usuario u : tablaUsuarios.obtenerTodos()) {

            // ListaSimple<Cancion>
            if (u.getListaFavoritos() == null) continue;

            for (Cancion c : u.getListaFavoritos()) {
                if (c == null) continue;

                String titulo = c.getTitulo();
                if (titulo == null) titulo = "Desconocido";

                conteo.put(titulo, conteo.getOrDefault(titulo, 0) + 1);
            }
        }

        List<Map<String, Object>> resultado = new ArrayList<>();

        conteo.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(10)
                .forEach(entry -> {
                    Map<String, Object> obj = new HashMap<>();
                    obj.put("key", entry.getKey());
                    obj.put("value", entry.getValue());
                    resultado.add(obj);
                });

        return resultado;
    }

    // ============================================================
    // 2️⃣ Artistas más populares (según favoritos)
    // ============================================================
    @GetMapping("/artistas_populares")
    public List<Map<String, Object>> artistasMasPopulares() {

        Map<String, Integer> conteo = new HashMap<>();

        for (Usuario u : tablaUsuarios.obtenerTodos()) {

            if (u.getListaFavoritos() == null) continue;

            for (Cancion c : u.getListaFavoritos()) {
                if (c == null) continue;

                String artista = c.getArtista();
                if (artista == null || artista.isBlank()) artista = "Desconocido";

                conteo.put(artista, conteo.getOrDefault(artista, 0) + 1);
            }
        }

        List<Map<String, Object>> resultado = new ArrayList<>();

        conteo.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(10)
                .forEach(entry -> {
                    Map<String, Object> obj = new HashMap<>();
                    obj.put("key", entry.getKey());
                    obj.put("value", entry.getValue());
                    resultado.add(obj);
                });

        return resultado;
    }
}
