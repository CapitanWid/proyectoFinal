package com.uniquindio.server.syncup.datastructures;


import java.util.HashMap;
import java.util.Map;

public class NodoTrie {
    // Cada hijo está identificado por una letra
    private Map<Character, NodoTrie> hijos;
    private boolean finPalabra;

    public NodoTrie() {
        this.hijos = new HashMap<>();
        this.finPalabra = false;
    }

    public Map<Character, NodoTrie> getHijos() {
        return hijos;
    }

    public boolean isFinPalabra() {
        return finPalabra;
    }

    public void setFinPalabra(boolean finPalabra) {
        this.finPalabra = finPalabra;
    }
}

