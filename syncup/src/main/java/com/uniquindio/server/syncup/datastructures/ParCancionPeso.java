package com.uniquindio.server.syncup.datastructures;

import com.uniquindio.server.syncup.model.Cancion;

public class ParCancionPeso implements Comparable<ParCancionPeso> {

    public Cancion cancion;
    public double peso;

    public ParCancionPeso(Cancion c, double p) {
        this.cancion = c;
        this.peso = p;
    }

    // Orden natural: menor peso primero
    @Override
    public int compareTo(ParCancionPeso otro) {
        return Double.compare(this.peso, otro.peso);
    }
}
