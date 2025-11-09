package com.uniquindio.server.syncup.model;

import java.util.LinkedList;

public class Usuario implements Comparable<Usuario> {

    private String usuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
    private TipoUsuario tipo;

    private LinkedList<Cancion> listaFavoritos;

    public Usuario(String usuario, String nombre, String apellido, String correo, String contrasena) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasena = contrasena;
        this.listaFavoritos = new LinkedList<>();
        this.tipo = TipoUsuario.NORMAL;
    }

    // --- Métodos para favoritos ---
    public LinkedList<Cancion> getListaFavoritos() { return listaFavoritos; }

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

    // --- Getters y Setters ---
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

    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }

    public boolean esAdmin() { return tipo == TipoUsuario.ADMIN; }

    // --- Implementación de Comparable ---
    @Override
    public int compareTo(Usuario otro) {
        // Comparamos por nombre de usuario (campo único)
        return this.usuario.compareToIgnoreCase(otro.usuario);
    }
}
