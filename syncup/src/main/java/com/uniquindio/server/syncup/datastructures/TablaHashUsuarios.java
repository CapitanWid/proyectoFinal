package com.uniquindio.server.syncup.datastructures;

import com.uniquindio.server.syncup.model.Usuario;

public class TablaHashUsuarios {

    private ListaSimple<Usuario>[] tabla;  // SOLO estructuras propias
    private int capacidad;

    @SuppressWarnings("unchecked")
    public TablaHashUsuarios(int capacidad) {
        this.capacidad = capacidad;
        this.tabla = new ListaSimple[capacidad];

        for (int i = 0; i < capacidad; i++) {
            tabla[i] = new ListaSimple<>();
        }
    }

    private int hash(String clave) {
        return Math.abs(clave.hashCode()) % capacidad;
    }

    // ===========================
    // AGREGAR USUARIO
    // ===========================
    public void agregarUsuario(Usuario usuario) {
        int index = hash(usuario.getUsuario());
        ListaSimple<Usuario> bucket = tabla[index];

        // Verificar duplicado
        for (Usuario u : bucket) {
            if (u.getUsuario().equalsIgnoreCase(usuario.getUsuario())) {
                throw new IllegalArgumentException("El usuario ya existe");
            }
        }

        bucket.agregarFinal(usuario);
    }

    // ===========================
    // BUSCAR
    // ===========================
    public Usuario buscarUsuario(String nombreUsuario) {
        int index = hash(nombreUsuario);
        ListaSimple<Usuario> bucket = tabla[index];

        for (Usuario u : bucket) {
            if (u.getUsuario().equalsIgnoreCase(nombreUsuario)) {
                return u;
            }
        }
        return null;
    }

    public boolean existeUsuario(String nombreUsuario) {
        return buscarUsuario(nombreUsuario) != null;
    }

    // ===========================
    // ELIMINAR
    // ===========================
    public void eliminarUsuario(String nombreUsuario) {
        int index = hash(nombreUsuario);
        ListaSimple<Usuario> bucket = tabla[index];

        int pos = bucket.obtenerPosicionNodo(
                new Usuario(nombreUsuario, "", "", "", "")
        );

        if (pos != -1) {
            bucket.eliminarEn(pos);
        }
    }

    // Alias
    public Usuario buscar(String nombreUsuario) {
        return buscarUsuario(nombreUsuario);
    }

    // ===========================
    // OBTENER TODOS
    // ===========================
    public ListaSimple<Usuario> obtenerTodos() {
        ListaSimple<Usuario> salida = new ListaSimple<>();

        for (ListaSimple<Usuario> bucket : tabla) {
            for (Usuario u : bucket) {
                salida.agregarFinal(u);
            }
        }
        return salida;
    }
}
