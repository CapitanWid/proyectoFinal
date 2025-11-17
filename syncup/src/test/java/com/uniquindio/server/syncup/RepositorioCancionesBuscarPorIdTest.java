package com.uniquindio.server.syncup;


import com.uniquindio.server.syncup.model.Cancion;
import com.uniquindio.server.syncup.service.RepositorioCanciones;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RepositorioCancionesBuscarPorIdTest {


    //Test para verificar que se puede recuperar una canción específica 
    //a partir de su identificador único.
    //Primero agrega una canción conocida al repositorio
    @Test
    void testBuscarPorId() {

        RepositorioCanciones repo = new RepositorioCanciones();

        Cancion c = new Cancion(
                "10", "MiTitulo", "MiArtista",
                "Pop", "2021", 180, "tema.mp3"
        );

        repo.agregarCancion(c);

        // Buscar la cancion por su ID
        Cancion resultado = repo.buscarPorId("10");

        // Validaciones
        assertNotNull(resultado); 
        assertEquals("MiTitulo", resultado.getTitulo());
        assertEquals("10", resultado.getId());
    }
}

