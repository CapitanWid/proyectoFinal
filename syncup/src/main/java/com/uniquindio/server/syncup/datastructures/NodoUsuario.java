package com.uniquindio.server.syncup.datastructures;

import com.uniquindio.server.syncup.model.Usuario;

public class NodoUsuario implements Comparable<NodoUsuario> {
    private Usuario usuario;
    private ListaSimple<NodoUsuario> amigos;

    public NodoUsuario(Usuario usuario) {
        this.usuario = usuario;
        this.amigos = new ListaSimple<>();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public ListaSimple<NodoUsuario> getAmigos() {
        return amigos;
    }

    // Seguir a otro usuario (no duplicados)
    public void seguir(NodoUsuario otro) {
        if (buscarAmigo(otro.getUsuario().getUsuario()) == null) {
            amigos.agregarFinal(otro);
        }
    }

    // Buscar amigo por nombre
    public NodoUsuario buscarAmigo(String nombre) {
        for (NodoUsuario n : amigos) {
            if (n.getUsuario().getUsuario().equalsIgnoreCase(nombre)) {
                return n;
            }
        }
        return null;
    }

    // Dejar de seguir
    public void dejarDeSeguir(NodoUsuario otro) {
        int pos = amigos.obtenerPosicionNodo(otro);
        if (pos != -1) {
            amigos.eliminarEn(pos);
        }
    }

    @Override
    public int compareTo(NodoUsuario otro) {
        // Comparamos por el nombre de usuario, por ejemplo
        return this.usuario.getUsuario().compareToIgnoreCase(otro.getUsuario().getUsuario());
    }
}
