package com.uniquindio.server.syncup;

import com.uniquindio.server.syncup.model.Cancion;
import com.uniquindio.server.syncup.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioEliminarFavoritoTest {

    @Test
    void testEliminarFavorito() {

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

        usuario.eliminarFavorito(cancion);

        assertEquals(0, usuario.getListaFavoritos().size());
        assertFalse(usuario.esFavorito(cancion));
    }
}
