package com.uniquindio.server.syncup.model;

import java.util.LinkedList;

public class Usuario {
    private String usuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;

    private LinkedList<Cancion> listaFavoritos; // Lista en memoria

    public Usuario(String usuario, String nombre, String apellido, String correo, String contrasena) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasena = contrasena;
        this.listaFavoritos = new LinkedList<>();
    }

    // --- Métodos para favoritos ---
    public LinkedList<Cancion> getListaFavoritos() {
        return listaFavoritos;
    }

    public void agregarFavorito(Cancion cancion) {
        if (!listaFavoritos.contains(cancion)) {
            listaFavoritos.add(cancion);
        }
    }

    public void eliminarFavorito(Cancion cancion) {
        listaFavoritos.remove(cancion);
    }

    public boolean esFavorito(Cancion cancion) {
        return listaFavoritos.contains(cancion);
    }

    // --- Getters y Setters básicos ---
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
