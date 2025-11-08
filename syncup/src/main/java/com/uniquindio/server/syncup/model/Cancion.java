package com.uniquindio.server.syncup.model;

import java.util.ArrayList;
import java.util.List;

public class Cancion implements Comparable<Cancion> {

    private String id; // RF-018: Identificador único
    private String titulo;
    private String artista;
    //private String album;
    private String genero;
    //private String compositor;
    private String anio;
    private int duracionSegundos;
    private String nombreArchivo;

    // RF-019: Relaciones del grafo
    private List<Cancion> similares;

    public Cancion(String id, String titulo, String artista, String genero,
                   String anio, int duracionSegundos, String nombreArchivo) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.genero = genero;
        this.anio = anio;
        this.duracionSegundos = duracionSegundos;
        this.nombreArchivo = nombreArchivo;
        this.similares = new ArrayList<>();
    }

    // --- Métodos de grafo ---
    public void agregarSimilitud(Cancion otra) {
        if (!similares.contains(otra)) {
            similares.add(otra);
        }
    }

    public List<Cancion> getSimilares() {
        return similares;
    }

    // --- Getters y setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getAnio() { return anio; }
    public void setAnio(String anio) { this.anio = anio; }

    public int getDuracionSegundos() { return duracionSegundos; }
    public void setDuracionSegundos(int duracionSegundos) { this.duracionSegundos = duracionSegundos; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    // --- RF-020: hashCode y equals basados en id ---
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cancion cancion = (Cancion) obj;
        return id != null && id.equals(cancion.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Cancion{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", artista='" + artista + '\'' +
                ", genero='" + genero + '\'' +
                ", anio='" + anio + '\'' +
                ", duracionSegundos=" + duracionSegundos +
                '}';
    }

    @Override
    public int compareTo(Cancion otra) {
        // Ordenar por título (puedes cambiarlo por ID si prefieres)
        return this.getTitulo().compareToIgnoreCase(otra.getTitulo());
    }

}
