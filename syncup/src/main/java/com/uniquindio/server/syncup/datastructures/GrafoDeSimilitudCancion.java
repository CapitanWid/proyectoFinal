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

    /**
     * Devuelve true si comparten al menos un artista
     * (soporta "feat.", "ft.", "&", " y ", comas)
     */
    static boolean artistasCompatibles(String a, String b) {
        if (a == null || b == null) return false;
        Set<String> sa = normalizarArtistas(a);
        Set<String> sb = normalizarArtistas(b);
        for (String x : sa) {
            if (sb.contains(x)) return true;
        }
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

/**
 * Grafo ponderado NO dirigido especializado para Cancion.
 */
public class GrafoDeSimilitudCancion {

    /** Mapa de claves a nodos Cancion (no se requiere equals/hashCode en Cancion). */
    private final Map<String, Cancion> nodoPorClave = new HashMap<>();

    /** Mapa de adyacencias: clave -> (claveVecina -> costo). */
    private final Map<String, Map<String, Double>> ady = new HashMap<>();

    /** Generador de clave unica de cada cancion (titulo|artista|anio). */
    private final Function<Cancion, String> keyFn = c ->
            (safe(c.getTitulo()) + "|" + safe(c.getArtista()) + "|" + safe(c.getAnio())).toLowerCase();

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    // ==========================================================
    // Métodos básicos
    // ==========================================================

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

    // ==========================================================
    // Construcción automática del grafo
    // ==========================================================

    /**
     * Construye un grafo conectando canciones que comparten genero o artista.
     * @param canciones Lista de canciones
     * @param umbralMatches 1 = (genero O artista) | 2 = (ambos)
     */
    public static GrafoDeSimilitudCancion construir(List<Cancion> canciones, int umbralMatches) {
        GrafoDeSimilitudCancion g = new GrafoDeSimilitudCancion();

        for (Cancion c : canciones) g.agregarNodo(c);

        for (int i = 0; i < canciones.size(); i++) {
            for (int j = i + 1; j < canciones.size(); j++) {
                Cancion a = canciones.get(i);
                Cancion b = canciones.get(j);
                int matches = 0;

                if (SimilaridadGA.generosIguales(a.getGenero(), b.getGenero())) matches++;
                if (SimilaridadGA.artistasCompatibles(a.getArtista(), b.getArtista())) matches++;

                if (matches >= umbralMatches) {
                    double costo = 1.0 - (matches / 2.0); // 0.5 si coincide 1; 0.0 si ambos
                    g.conectarNoDirigido(a, b, costo);
                }
            }
        }

        return g;
    }

    // ==========================================================
    // Algoritmo Dijkstra (costos minimos desde un origen)
    // ==========================================================

    public Map<Cancion, Double> dijkstra(Cancion origen) {
        String ko = keyFn.apply(origen);
        if (!ady.containsKey(ko)) agregarNodo(origen);

        Map<String, Double> dist = new HashMap<>();
        for (String k : ady.keySet()) {
            dist.put(k, Double.POSITIVE_INFINITY);
        }
        dist.put(ko, 0.0);

        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        pq.add(ko);

        while (!pq.isEmpty()) {
            String u = pq.poll();
            for (Map.Entry<String, Double> e : ady.getOrDefault(u, Map.of()).entrySet()) {
                String v = e.getKey();
                double alt = dist.get(u) + e.getValue();
                if (alt < dist.get(v)) {
                    dist.put(v, alt);
                    pq.remove(v);
                    pq.add(v);
                }
            }
        }

        Map<Cancion, Double> res = new HashMap<>();
        for (Map.Entry<String, Double> e : dist.entrySet()) {
            res.put(nodoPorClave.get(e.getKey()), e.getValue());
        }
        return res;
    }

    // ==========================================================
    // Vecinos directos (similares)
    // ==========================================================

    public List<Map.Entry<Cancion, Double>> vecinosOrdenados(Cancion c) {
        String k = keyFn.apply(c);
        Map<String, Double> m = ady.getOrDefault(k, Map.of());
        List<Map.Entry<Cancion, Double>> out = new ArrayList<>();
        for (Map.Entry<String, Double> e : m.entrySet()) {
            out.add(Map.entry(nodoPorClave.get(e.getKey()), e.getValue()));
        }
        out.sort(Map.Entry.comparingByValue()); // menor costo primero
        return out;
    }

    // ==========================================================
    // Recomendaciones (top N similares)
    // ==========================================================

    /**
     * Devuelve las N canciones mas similares a la cancion dada.
     */
    public List<Cancion> recomendarCanciones(Cancion base, int n) {
        List<Map.Entry<Cancion, Double>> vecinos = vecinosOrdenados(base);
        List<Cancion> recomendadas = new ArrayList<>();
        for (int i = 0; i < Math.min(n, vecinos.size()); i++) {
            recomendadas.add(vecinos.get(i).getKey());
        }
        return recomendadas;
    }
}

