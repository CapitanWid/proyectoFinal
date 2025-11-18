package com.uniquindio.server.syncup.service;

import com.uniquindio.server.syncup.datastructures.GrafoDeSimilitudCancion;
import com.uniquindio.server.syncup.datastructures.ListaSimple;
import com.uniquindio.server.syncup.model.Cancion;

public class GrafoSimilitudService {

    private static final RepositorioCanciones repo = new RepositorioCanciones();
    private static GrafoDeSimilitudCancion grafo;

    public static void construirGrafo() {

        // 1. Obtener lista propia
        ListaSimple<Cancion> lista = repo.getListaCanciones();

        // 2. Convertir ListaSimple → arreglo
        Cancion[] arreglo = lista.toArray(Cancion.class);

        // 3. Construir grafo permitido
        grafo = GrafoDeSimilitudCancion.construir(arreglo, 1);

        System.out.println("Grafo de similitud construido con " + arreglo.length + " canciones.");
    }

    /**
     * Recomienda las N canciones más similares usando estructuras propias.
     */
    public static ListaSimple<Cancion> recomendar(Cancion base, int n) {
        if (grafo == null) construirGrafo();
        return grafo.recomendarCancionesPropio(base, n);
    }

    public static GrafoDeSimilitudCancion getGrafo() {
        if (grafo == null) construirGrafo();
        return grafo;
    }
}
