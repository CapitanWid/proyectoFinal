package com.uniquindio.server.syncup.datastructures;

import java.util.Map;

public class Trie {

    private NodoTrie raiz = new NodoTrie();

    // Insertar palabra en el trie
    public void insertar(String palabra) {
        NodoTrie nodo = raiz;
        for (char c : palabra.toLowerCase().toCharArray()) {
            nodo.getHijos().putIfAbsent(c, new NodoTrie());
            nodo = nodo.getHijos().get(c);
        }
        nodo.setFinPalabra(true);
    }

    // Autocompletar usando SOLO ListaSimple<String>
    public ListaSimple<String> autocompletar(String prefijo) {

        ListaSimple<String> resultados = new ListaSimple<>();
        NodoTrie nodo = raiz;

        // Navegar hasta el nodo final del prefijo
        for (char c : prefijo.toLowerCase().toCharArray()) {
            if (!nodo.getHijos().containsKey(c)) {
                return resultados; // lista vacía, pero válida
            }
            nodo = nodo.getHijos().get(c);
        }

        // Recolectar todas las palabras desde aquí
        recolectar(nodo, new StringBuilder(prefijo), resultados);

        return resultados;
    }

    // Recolección recursiva usando ListaSimple<String>
    private void recolectar(NodoTrie nodo, StringBuilder actual, ListaSimple<String> resultados) {

        if (nodo.isFinPalabra()) {
            resultados.agregarFinal(actual.toString());
        }

        // Recorrer todos los hijos (MAP SÍ se puede usar)
        for (Map.Entry<Character, NodoTrie> e : nodo.getHijos().entrySet()) {
            actual.append(e.getKey());
            recolectar(e.getValue(), actual, resultados);
            actual.deleteCharAt(actual.length() - 1); // retroceder
        }
    }
}
