package com.uniquindio.server.syncup.datastructures;


import java.util.*;

import com.uniquindio.server.syncup.model.Usuario;

public class GrafoSocial {
    private ListaSimple<NodoUsuario> nodos;

    public GrafoSocial() {
        nodos = new ListaSimple<>();
    }

    public void agregarUsuario(Usuario u) {
        if (buscarNodo(u.getUsuario()) == null) {
            nodos.agregarFinal(new NodoUsuario(u));
        }
    }

    public NodoUsuario buscarNodo(String nombre) {
        for (NodoUsuario n : nodos) {
            if (n.getUsuario().getUsuario().equalsIgnoreCase(nombre)) {
                return n;
            }
        }
        return null;
    }

    // Seguir (no dirigido si quieres amistad mutua, o solo dirigido si es tipo “seguir” estilo Twitter)
    public void seguir(String u1, String u2) {
        NodoUsuario n1 = buscarNodo(u1);
        NodoUsuario n2 = buscarNodo(u2);

        if (n1 == null || n2 == null) {
            throw new IllegalArgumentException("Uno de los usuarios no existe");
        }

        n1.seguir(n2);
        n2.seguir(n1); // si quieres amistad mutua; si es solo seguir tipo Twitter, elimina esta línea
    }

    public void dejarDeSeguir(String u1, String u2) {
        NodoUsuario n1 = buscarNodo(u1);
        NodoUsuario n2 = buscarNodo(u2);

        if (n1 != null && n2 != null) {
            n1.dejarDeSeguir(n2);
            n2.dejarDeSeguir(n1); // si quieres eliminar mutua
        }
    }

    // BFS para amigos de amigos
    public ListaSimple<Usuario> bfsAmigos(String nombreInicio, int nivelMaximo) {
        ListaSimple<Usuario> resultado = new ListaSimple<>();
        NodoUsuario inicio = buscarNodo(nombreInicio);
        if (inicio == null) return resultado;

        ListaSimple<NodoUsuario> visitados = new ListaSimple<>();
        Queue<NodoUsuario> cola = new LinkedList<>();
        Map<NodoUsuario, Integer> niveles = new HashMap<>();

        cola.add(inicio);
        niveles.put(inicio, 0);

        while (!cola.isEmpty()) {
            NodoUsuario actual = cola.poll();
            int nivel = niveles.get(actual);

            if (nivel > 0) resultado.agregarFinal(actual.getUsuario());

            if (nivel < nivelMaximo) {
                for (NodoUsuario amigo : actual.getAmigos()) {
                    if (visitados.obtenerPosicionNodo(amigo) == -1) {
                        cola.add(amigo);
                        niveles.put(amigo, nivel + 1);
                        visitados.agregarFinal(amigo);
                    }
                }
            }
        }

        return resultado;
    }

    public List<Usuario> getUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        for (NodoUsuario nodo : nodos) {
            lista.add(nodo.getUsuario());
        }
        return lista;
    }

}
