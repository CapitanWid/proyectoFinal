package com.uniquindio.server.syncup.datastructures;

import com.uniquindio.server.syncup.model.Cancion;

import java.util.*;
import java.util.function.Function;

/**
 * Reglas de similitud basadas en genero y artista.
 */
final class SimilaridadGA {

    static boolean generosIguales(String g1, String g2) {
        if (g1 == null || g2 == null) return false;
        return g1.trim().equalsIgnoreCase(g2.trim());
    }

    static boolean artistasCompatibles(String a, String b) {
        if (a == null || b == null) return false;
        Set<String> sa = normalizarArtistas(a);
        Set<String> sb = normalizarArtistas(b);
        for (String x : sa) if (sb.contains(x)) return true;
        return false;
    }

    private static Set<String> normalizarArtistas(String s) {
        String t = s.toLowerCase()
                .replace("feat.", ",")
                .replace("featuring", ",")
                .replace(" ft.", ",")
                .replace("&", ",")
                .replace(" y ", ",");

        String[] toks = t.split("[,;/]+");
        Set<String> out = new HashSet<>();

        for (String k : toks) {
            k = k.trim();
            if (!k.isEmpty()) out.add(k);
        }

        if (out.isEmpty() && !t.trim().isEmpty()) out.add(t.trim());
        return out;
    }
}

public class GrafoDeSimilitudCancion {

    private final Map<String, Cancion> nodoPorClave = new HashMap<>();
    private final Map<String, Map<String, Double>> ady = new HashMap<>();

    private final Function<Cancion, String> keyFn = c ->
            (safe(c.getTitulo()) + "|" + safe(c.getArtista()) + "|" + safe(c.getAnio())).toLowerCase();

    private static String safe(String s) { return (s == null ? "" : s.trim()); }

    // ======================================================================
    // Métodos básicos
    // ======================================================================

    public void agregarNodo(Cancion c) {
        String k = keyFn.apply(c);
        nodoPorClave.putIfAbsent(k, c);
        ady.putIfAbsent(k, new HashMap<>());
    }

    public void conectarNoDirigido(Cancion a, Cancion b, double costo) {
        String ka = keyFn.apply(a);
        String kb = keyFn.apply(b);
        agregarNodo(a);
        agregarNodo(b);
        ady.get(ka).put(kb, costo);
        ady.get(kb).put(ka, costo);
    }

    // ======================================================================
    // Construcción
    // ======================================================================

    public static GrafoDeSimilitudCancion construir(Cancion[] canciones, int umbralMatches) {

        GrafoDeSimilitudCancion g = new GrafoDeSimilitudCancion();
        int n = canciones.length;

        for (int i = 0; i < n; i++) g.agregarNodo(canciones[i]);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {

                Cancion a = canciones[i];
                Cancion b = canciones[j];
                int matches = 0;

                if (SimilaridadGA.generosIguales(a.getGenero(), b.getGenero())) matches++;
                if (SimilaridadGA.artistasCompatibles(a.getArtista(), b.getArtista())) matches++;

                if (matches >= umbralMatches) {
                    double costo = 1.0 - (matches / 2.0);
                    g.conectarNoDirigido(a, b, costo);
                }
            }
        }

        return g;
    }

    // ======================================================================
    // Vecinos ordenados — SOLO ListaSimple
    // ======================================================================

    public ListaSimple<ParCancionPeso> vecinosOrdenadosPropio(Cancion c) {

        ListaSimple<ParCancionPeso> resultado = new ListaSimple<>();

        String k = keyFn.apply(c);
        Map<String, Double> m = ady.getOrDefault(k, Map.of());

        for (Map.Entry<String, Double> e : m.entrySet()) {
            Cancion destino = nodoPorClave.get(e.getKey());
            resultado.agregarFinal(new ParCancionPeso(destino, e.getValue()));
        }

        // Ordenar burbuja
        int n = resultado.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                ParCancionPeso a = resultado.get(j);
                ParCancionPeso b = resultado.get(j + 1);

                if (a.peso > b.peso) {
                    resultado.modificarNodo(j, b);
                    resultado.modificarNodo(j + 1, a);
                }
            }
        }

        return resultado;
    }

    // ======================================================================
    // Recomendaciones TOTALMENTE PROPIAS
    // ======================================================================

    public ListaSimple<Cancion> recomendarCancionesPropio(Cancion base, int n) {

        ListaSimple<ParCancionPeso> vecinos = vecinosOrdenadosPropio(base);
        ListaSimple<Cancion> resultado = new ListaSimple<>();

        int limite = Math.min(n, vecinos.size());
        for (int i = 0; i < limite; i++) {
            resultado.agregarFinal(vecinos.get(i).cancion);
        }

        return resultado;
    }
}
