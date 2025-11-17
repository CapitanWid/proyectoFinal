package com.uniquindio.server.syncup;

import com.uniquindio.server.syncup.model.Cancion;
import com.uniquindio.server.syncup.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioAgregarFavoritoTest {

    @Test
    void testAgregarFavorito() {

        Usuario usuario = new Usuario(
                "user1",
                "Juan",
                "Perez",
                "correo@example.com",
                "pwd123"
        );

        Cancion cancion = new Cancion(
                "1", "Song1", "Artista", "Pop", "2020", 120, "song1.mp3"
        );

        usuario.agregarFavorito(cancion);

        assertEquals(1, usuario.getListaFavoritos().size());
        assertTrue(usuario.esFavorito(cancion));
    }
}
