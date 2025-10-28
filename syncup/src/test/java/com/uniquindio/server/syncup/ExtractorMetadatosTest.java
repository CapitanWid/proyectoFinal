package com.uniquindio.server.syncup;

import com.uniquindio.server.syncup.model.Cancion;
import com.uniquindio.server.syncup.service.ExtractorMetadatos;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class ExtractorMetadatosTest {

    @Test
    void testExtraerMetadatos() {
        File archivoPrueba = new File("D:\\datos_syncup\\songs\\Alan Walker - Alone.mp3");

        Cancion cancion = ExtractorMetadatos.extraerDesdeArchivo(archivoPrueba);

        assertNotNull(cancion);

        System.out.println("Título extraído: " + cancion.getTitulo());
        System.out.println("Artista extraído: " + cancion.getArtista());
        System.out.println("Duración (segundos): " + cancion.getDuracionSegundos());

        assertEquals("Alone", cancion.getTitulo());
        //assertEquals("Alan Walker ", cancion.getArtista());
        assertTrue(cancion.getDuracionSegundos() > 0);
    }
}
