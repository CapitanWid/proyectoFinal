package com.uniquindio.server.syncup.datastructures;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/*
 Lista simple genérica
 */
public class ListaSimple<T extends Comparable<T>> implements Iterable<T> {

    private Nodo<T> firstNode;
    private int size;

    // Constructor
    public ListaSimple() {
        this.firstNode = null;
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Nodo<T> actual = firstNode;
        for (int i = 0; i < index; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

    // Agregar al final SIN usar cola
    public void agregarFinal(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);

        if (size==0) {
            firstNode = nuevoNodo;
        } else {
            Nodo<T> actual = firstNode;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevoNodo);
        }

        size++;
    }

    public void agregarInicio(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        nuevoNodo.setSiguiente(firstNode);
        firstNode = nuevoNodo;
        size++;
    }

    public void agregarEn(T dato, int indice) {
        if (indice < 0 || indice > size) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }

        if (indice == 0) {
            agregarInicio(dato);
            return;
        }

        Nodo<T> actual = firstNode;
        for (int i = 0; i < indice - 1; i++) {
            actual = actual.getSiguiente();
        }

        Nodo<T> nuevoNodo = new Nodo<>(dato);
        nuevoNodo.setSiguiente(actual.getSiguiente());
        actual.setSiguiente(nuevoNodo);

        size++;
    }

    public T eliminarPrimero() {
        if (size==0) {
            throw new NoSuchElementException("La lista está vacía");
        }

        T dato = firstNode.getDato();
        firstNode = firstNode.getSiguiente();
        size--;

        return dato;
    }

    public T eliminarUltimo() {
        if (size==0) {
            throw new NoSuchElementException("La lista está vacía");
        }

        if (firstNode.getSiguiente() == null) {
            return eliminarPrimero();
        }

        Nodo<T> actual = firstNode;
        while (actual.getSiguiente().getSiguiente() != null) {
            actual = actual.getSiguiente();
        }

        T dato = actual.getSiguiente().getDato();
        actual.setSiguiente(null);
        size--;

        return dato;
    }

    public T eliminarEn(int indice) {
        if (indice < 0 || indice >= size) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }

        if (indice == 0) {
            return eliminarPrimero();
        }

        Nodo<T> actual = firstNode;
        for (int i = 0; i < indice - 1; i++) {
            actual = actual.getSiguiente();
        }

        T dato = actual.getSiguiente().getDato();
        actual.setSiguiente(actual.getSiguiente().getSiguiente());
        size--;

        return dato;
    }

    public T obtenerValorNodo(int indice) {
        if (!indiceValido(indice)) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }

        Nodo<T> actual = firstNode;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }

        return actual.getDato();
    }

    public Nodo<T> obtenerNodo(int index) {
        if (!indiceValido(index)) {
            throw new IndexOutOfBoundsException("Índice fuera de rango");
        }

        Nodo<T> actual = firstNode;
        for (int i = 0; i < index; i++) {
            actual = actual.getSiguiente();
        }
        return actual;
    }

    public int obtenerPosicionNodo(T dato) {
        Nodo<T> actual = firstNode;
        int posicion = 0;

        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                return posicion;
            }
            actual = actual.getSiguiente();
            posicion++;
        }

        return -1; // No encontrado
    }

    public boolean indiceValido(int indice) {
        return indice >= 0 && indice < size;
    }

    public void modificarNodo(int posicion, T nuevoDato) {
        if (!indiceValido(posicion)) {
            throw new IndexOutOfBoundsException("Índice fuera de rango");
        }

        Nodo<T> actual = firstNode;
        for (int i = 0; i < posicion; i++) {
            actual = actual.getSiguiente();
        }

        actual.setDato(nuevoDato);
    }

    public void borrarLista() {
        firstNode = null;
        size = 0;
    }

    public void ordenarLista() {
        if (firstNode == null || firstNode.getSiguiente() == null) {
            return;
        }

        boolean cambiado;
        do {
            cambiado = false;
            Nodo<T> actual = firstNode;

            while (actual.getSiguiente() != null) {
                Nodo<T> siguiente = actual.getSiguiente();

                if (actual.getDato().compareTo(siguiente.getDato()) > 0) {
                    T temp = actual.getDato();
                    actual.setDato(siguiente.getDato());
                    siguiente.setDato(temp);
                    cambiado = true;
                }

                actual = actual.getSiguiente();
            }
        } while (cambiado);
    }

    public void imprimirLista() {
        Nodo<T> actual = firstNode;
        while (actual != null) {
            System.out.print(actual.getDato() + " -> ");
            actual = actual.getSiguiente();
        }
        System.out.println("null");
    }

    public boolean estaVacia() {
        return size == 0;
    }


    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = firstNode;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                T dato = actual.getDato();
                actual = actual.getSiguiente();
                return dato;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Nodo<T> actual = firstNode;

        while (actual != null) {
            sb.append(actual.getDato());
            if (actual.getSiguiente() != null) {
                sb.append(", ");
            }
            actual = actual.getSiguiente();
        }

        sb.append("]");
        return sb.toString();
    }

    public List<T> toList() {
        List<T> lista = new ArrayList<>();
        Nodo<T> actual = firstNode;

        while (actual != null) {
            lista.add(actual.getDato());
            actual = actual.getSiguiente();
        }

        return lista;
    }    

   
    public Object[] toArrayObject() {
        Object[] arr = new Object[size];
        Nodo<T> actual = firstNode;
        int i = 0;

        while (actual != null) {
            arr[i++] = actual.getDato();
            actual = actual.getSiguiente();
        }

        return arr;
    }

    @SuppressWarnings("unchecked")
    public T[] toArray(Class<T> tipo) {
        T[] arr = (T[]) java.lang.reflect.Array.newInstance(tipo, size);

        Nodo<T> actual = firstNode;
        int i = 0;
        while (actual != null) {
            arr[i++] = actual.getDato();
            actual = actual.getSiguiente();
        }

        return arr;
    }








}

