package com.uniquindio.server.syncup.datastructures;

import com.uniquindio.server.syncup.model.Usuario;
import java.util.LinkedList;

public class TablaHashUsuarios {

    private LinkedList<Usuario>[] tabla;
    private int capacidad;

    public TablaHashUsuarios(int capacidad) {
        this.capacidad = capacidad;
        this.tabla = new LinkedList[capacidad];
        for (int i = 0; i < capacidad; i++) {
            tabla[i] = new LinkedList<>();
        }
    }

    private int hash(String clave) {
        return Math.abs(clave.hashCode()) % capacidad;
    }

    public void agregarUsuario(Usuario usuario) {
        int index = hash(usuario.getUsuario());
        for (Usuario u : tabla[index]) {
            if (u.getUsuario().equalsIgnoreCase(usuario.getUsuario())) {
                throw new IllegalArgumentException("El usuario ya existe");
            }
        }
        tabla[index].add(usuario);
    }

    public Usuario buscarUsuario(String nombreUsuario) {
        int index = hash(nombreUsuario);
        for (Usuario u : tabla[index]) {
            if (u.getUsuario().equalsIgnoreCase(nombreUsuario)) {
                return u;
            }
        }
        return null;
    }

    public boolean existeUsuario(String nombreUsuario) {
        return buscarUsuario(nombreUsuario) != null;
    }

    public void eliminarUsuario(String nombreUsuario) {
        int index = hash(nombreUsuario);
        tabla[index].removeIf(u -> u.getUsuario().equalsIgnoreCase(nombreUsuario));
    }

    // Alias adicional para que otros controladores usen el mismo método
    public Usuario buscar(String nombreUsuario) {
        return buscarUsuario(nombreUsuario);
    }

    // Método opcional: obtener todos los usuarios
    public LinkedList<Usuario> obtenerTodos() {
        LinkedList<Usuario> lista = new LinkedList<>();
        for (LinkedList<Usuario> bucket : tabla) {
            lista.addAll(bucket);
        }
        return lista;
    }
}
