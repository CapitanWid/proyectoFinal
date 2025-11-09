package com.uniquindio.server.syncup.datastructures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Trie {

    private NodoTrie raiz = new NodoTrie();

    public void insertar(String palabra) {
        NodoTrie nodo = raiz;
        for (char c : palabra.toLowerCase().toCharArray()) {
            nodo.getHijos().putIfAbsent(c, new NodoTrie());
            nodo = nodo.getHijos().get(c);
        }
        nodo.setFinPalabra(true);
    }

    public List<String> autocompletar(String prefijo) {
        List<String> resultados = new ArrayList<>();
        NodoTrie nodo = raiz;

        // Navegar hasta el nodo del prefijo
        for (char c : prefijo.toLowerCase().toCharArray()) {
            if (!nodo.getHijos().containsKey(c)) {
                return resultados; // no hay coincidencias
            }
            nodo = nodo.getHijos().get(c);
        }

        recolectar(nodo, new StringBuilder(prefijo), resultados);
        return resultados;
    }

    private void recolectar(NodoTrie nodo, StringBuilder actual, List<String> resultados) {
        if (nodo.isFinPalabra()) {
            resultados.add(actual.toString());
        }

        for (Map.Entry<Character, NodoTrie> e : nodo.getHijos().entrySet()) {
            recolectar(e.getValue(), new StringBuilder(actual).append(e.getKey()), resultados);
        }
    }
}

