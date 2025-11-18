package com.uniquindio.server.syncup.model;

import com.uniquindio.server.syncup.datastructures.ListaSimple;

public class Usuario implements Comparable<Usuario> {

    private String usuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
    private TipoUsuario tipo;

    private ListaSimple<Cancion> listaFavoritos;

    public Usuario(String usuario, String nombre, String apellido, String correo, String contrasena) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasena = contrasena;

        // Estructura propia
        this.listaFavoritos = new ListaSimple<>();
        this.tipo = TipoUsuario.NORMAL;
    }

    // =============================
    // MÉTODOS PARA FAVORITOS
    // =============================

    public ListaSimple<Cancion> getListaFavoritos() {
        return listaFavoritos;
    }

    public void agregarFavorito(Cancion cancion) {
        if (cancion == null) return;

        // Evitar repetidos
        if (listaFavoritos.obtenerPosicionNodo(cancion) == -1) {
            listaFavoritos.agregarFinal(cancion);
        }
    }

    public void eliminarFavorito(Cancion cancion) {
        if (cancion == null) return;

        int pos = listaFavoritos.obtenerPosicionNodo(cancion);
        if (pos != -1) {
            listaFavoritos.eliminarEn(pos);
        }
    }

    public boolean esFavorito(Cancion cancion) {
        return listaFavoritos.obtenerPosicionNodo(cancion) != -1;
    }

    // =============================
    // GETTERS Y SETTERS
    // =============================

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

    // =============================
    // COMPARABLE
    // =============================

    @Override
    public int compareTo(Usuario otro) {
        return this.usuario.compareToIgnoreCase(otro.usuario);
    }
}
