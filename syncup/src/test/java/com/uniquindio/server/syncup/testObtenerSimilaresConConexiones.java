package com.uniquindio.server.syncup;


import com.uniquindio.server.syncup.model.Cancion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CancionSimilaresTest {

    //Prueba que el sistema de similitud entre canciones funciona correctamente.

    @Test
    void testObtenerSimilaresConConexiones() {

        Cancion a = new Cancion(
                "1", "A", "X", "Pop", "2021", 100, "a.mp3"
        );
        Cancion b = new Cancion(
                "2", "B", "Y", "Rock", "2020", 120, "b.mp3"
        );
        Cancion c = new Cancion(
                "3", "C", "Z", "Jazz", "2019", 140, "c.mp3"
        );

        // Conexiones de similitud
        a.agregarSimilitud(b);
        a.agregarSimilitud(c);

        // Validar tamaño
        assertEquals(2, a.getSimilares().size());

        // Validar que contiene las canciones correctas
        assertTrue(a.getSimilares().contains(b));
        assertTrue(a.getSimilares().contains(c));
    }
}
