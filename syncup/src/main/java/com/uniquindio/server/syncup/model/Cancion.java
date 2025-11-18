package com.uniquindio.server.syncup.model;

import com.uniquindio.server.syncup.datastructures.ListaSimple;

public class Cancion implements Comparable<Cancion> {

    private String id;
    private String titulo;
    private String artista;
    private String genero;
    private String anio;
    private int duracionSegundos;
    private String nombreArchivo;

    // Estructura de datos propia
    private ListaSimple<Cancion> similares;

    public Cancion(String id, String titulo, String artista, String genero,
                   String anio, int duracionSegundos, String nombreArchivo) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.genero = genero;
        this.anio = anio;
        this.duracionSegundos = duracionSegundos;
        this.nombreArchivo = nombreArchivo;

        // Crear lista propia
        this.similares = new ListaSimple<>();
    }

    // --- Métodos del grafo ---
    public void agregarSimilitud(Cancion otra) {

        // Evitar duplicados usando equals(id)
        if (similares.obtenerPosicionNodo(otra) == -1) {
            similares.agregarFinal(otra);
        }
    }

    // Getter actualizado
    public ListaSimple<Cancion> getSimilares() {
        return similares;
    }

    // --- Getters y setters restantes ---
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

    // --- equals y hashCode basados en id ---
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
        return this.getTitulo().compareToIgnoreCase(otra.getTitulo());
    }
}
