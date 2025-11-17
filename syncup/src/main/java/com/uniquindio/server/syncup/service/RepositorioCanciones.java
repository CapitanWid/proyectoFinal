package com.uniquindio.server.syncup.service;

import com.uniquindio.server.syncup.datastructures.ListaSimple;
import com.uniquindio.server.syncup.datastructures.Trie;
import com.uniquindio.server.syncup.model.Cancion;
import com.uniquindio.server.syncup.dto.FiltroRequest;


import java.io.File;
import java.util.List;

public class RepositorioCanciones {

    private final ListaSimple<Cancion> listaCanciones = new ListaSimple<>();
    private final String carpetaCanciones = "D:\\datos_syncup\\songs\\";
    private Trie trieTitulos = new Trie();


    public RepositorioCanciones() {
        cargarCancionesDesdeCarpeta();

        for (Cancion c : listaCanciones) {
            if (c != null && c.getTitulo() != null) {
                trieTitulos.insertar(c.getTitulo().toLowerCase());
                System.out.println("🔥 Insertado en Trie (inicio): " + c.getTitulo());
            }
        }    
    }

    private void cargarCancionesDesdeCarpeta() {
        File carpeta = new File(carpetaCanciones);
        if (!carpeta.exists() || !carpeta.isDirectory()) {
            System.err.println("⚠ Carpeta de canciones no encontrada: " + carpetaCanciones);
            return;
        }

        File[] archivos = carpeta.listFiles((dir, name) -> name.endsWith(".mp3"));
        if (archivos == null) return;

        for (File archivo : archivos) {
            Cancion cancion = ExtractorMetadatos.extraerDesdeArchivo(archivo);
            if (cancion != null) {
                listaCanciones.agregarFinal(cancion);
            }
        }

        System.out.println("🎵 Canciones cargadas en memoria: " + listaCanciones.size());
    }

    public ListaSimple<Cancion> getListaCanciones() {
        return listaCanciones;
    }
/* 
    public void agregarCancion(Cancion cancion) {
        listaCanciones.agregarFinal(cancion);
    }
*/
    // Para autocompletar canciones por trie:
    public void agregarCancion(Cancion c) {
        listaCanciones.agregarFinal(c);

        // Insertar automáticamente el título en el Trie
        if (c.getTitulo() != null) {
            trieTitulos.insertar(c.getTitulo().toLowerCase());
            System.out.println("🔥 Insertado en Trie: " + c.getTitulo());
        } else {
        System.out.println("⚠️ Canción sin título: " + c);
     }
    }

    public List<String> autocompletarTitulos(String prefijo) {
        if (prefijo == null || prefijo.isBlank()) {
            return List.of();
        }
        return trieTitulos.autocompletar(prefijo.toLowerCase());
    }


    public Cancion buscarPorTitulo(String titulo) {
        for (Cancion c : listaCanciones) {
            if (c.getTitulo().equalsIgnoreCase(titulo)) {
                return c;
            }
        }
        return null;
    }

    public ListaSimple<Cancion> buscarPorCriterios(String titulo, ListaSimple<FiltroRequest> filtros) {
        ListaSimple<Cancion> resultados = new ListaSimple<>();

        for (Cancion c : listaCanciones) {

            boolean cumpleTitulo = titulo == null || titulo.isBlank() || c.getTitulo().toLowerCase().contains(titulo.toLowerCase());
            boolean cumpleFiltros = true;

            if (filtros != null && filtros.size() > 0) {
                boolean temp = filtros.get(0) != null && "OR".equalsIgnoreCase(filtros.get(0).getLogica()) ? false : true;

                for (FiltroRequest f : filtros) {
                    boolean cumple = false;

                    if (f.getValor() == null || f.getValor().isBlank()) continue;

                    switch (f.getCampo().toLowerCase()) {
                        case "artista":
                            cumple = c.getArtista().toLowerCase().contains(f.getValor().toLowerCase());
                            break;
                        case "genero":
                            cumple = c.getGenero().toLowerCase().contains(f.getValor().toLowerCase());
                            break;
                        case "anio":
                            cumple = String.valueOf(c.getAnio()).equals(f.getValor());
                            break;
                    }

                    // Aplicar lógica AND/OR
                    if ("AND".equalsIgnoreCase(f.getLogica())) {
                        temp = temp && cumple;
                    } else if ("OR".equalsIgnoreCase(f.getLogica())) {
                        temp = temp || cumple;
                    }
                }

                cumpleFiltros = temp;
            }

            if (cumpleTitulo && cumpleFiltros) {
                resultados.agregarFinal(c);
            }
        }

        return resultados;
    }

    public boolean eliminarCancionPorId(String id) {
        if (listaCanciones.estaVacia()) return false;

        int indice = 0;
        for (Cancion c : listaCanciones) {
            if (c.getId().equals(id)) {
                listaCanciones.eliminarEn(indice);
                System.out.println("🗑 Canción eliminada: " + c.getTitulo());
                return true;
            }
            indice++;
        }
        return false; // No encontrada
    }


    public Cancion buscarPorId(String id) {
        for (Cancion c : listaCanciones) {
            if (c != null && c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }    

}
