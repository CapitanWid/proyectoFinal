package com.uniquindio.server.syncup.service;

import com.uniquindio.server.syncup.datastructures.GrafoDeSimilitudCancion;
import com.uniquindio.server.syncup.model.Cancion;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que construye y maneja el grafo de similitud de canciones.
 * Se basa en genero y artista.
 */
public class GrafoSimilitudService {

    // Repositorio compartido (instancia unica)
    private static final RepositorioCanciones repo = new RepositorioCanciones();

    // Instancia del grafo actual
    private static GrafoDeSimilitudCancion grafo;

    /**
     * Construye o reconstruye el grafo completo a partir de las canciones actuales.
     */
    public static void construirGrafo() {
        // ✅ Recorremos la ListaSimple y la convertimos a una lista Java normal
        List<Cancion> canciones = new ArrayList<>();
        for (Cancion c : repo.getListaCanciones()) {
            canciones.add(c);
        }

        grafo = GrafoDeSimilitudCancion.construir(canciones, 1); // 1 = genero o artista
        System.out.println("✅ Grafo de similitud construido con " + canciones.size() + " canciones.");
    }

    /**
     * Recomienda las N canciones mas similares a la base.
     */
    public static List<Cancion> recomendar(Cancion base, int n) {
        if (grafo == null) construirGrafo();
        return grafo.recomendarCanciones(base, n);
    }

    /**
     * Metodo auxiliar (opcional) para obtener el grafo actual.
     */
    public static GrafoDeSimilitudCancion getGrafo() {
        if (grafo == null) construirGrafo();
        return grafo;
    }
}
