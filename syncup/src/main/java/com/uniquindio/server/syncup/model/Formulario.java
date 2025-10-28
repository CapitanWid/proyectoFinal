package com.uniquindio.server.syncup.model;

public class Formulario {
    private String nombre;
    private String apellido;
    private String correo;
    private String usuario;
    private String contrasena;
    private String repetirContrasena;

    // Getters y setters
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRepetirContrasena() {
        return repetirContrasena;
    }
    public void setRepetirContrasena(String repetirContrasena) {
        this.repetirContrasena = repetirContrasena;
    }

    @Override
    public String toString() {
        return "Nombre: " + nombre + 
               ", Apellido: " + apellido + 
               ", Correo: " + correo + 
               ", Usuario: " + usuario + 
               ", Contrasena: " + contrasena + 
               ", RepetirContrasena: " + repetirContrasena;
    }
}

