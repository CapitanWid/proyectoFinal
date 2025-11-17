package com.uniquindio.server.syncup;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.uniquindio.server.syncup.model.Cancion;
import com.uniquindio.server.syncup.service.RepositorioCanciones;

class RepositorioCancionesBuscarPorTituloTest {

    @Test
    void testBuscarPorTitulo() {
        RepositorioCanciones repo = new RepositorioCanciones();

        repo.agregarCancion(
            new Cancion("1", "Hola Mundo", "X", "Pop", "2020", 120, "a.mp3")
        );

        Cancion resultado = repo.buscarPorTitulo("Hola Mundo");

        assertNotNull(resultado);
        assertEquals("Hola Mundo", resultado.getTitulo());
    }
}
