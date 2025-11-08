package com.uniquindio.server.syncup.service;

import com.uniquindio.server.syncup.model.Cancion;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.FieldKey;

import java.io.File;

public class ExtractorMetadatos {

    public static Cancion extraerDesdeArchivo(File archivo) {
        try {
            AudioFile audioFile = AudioFileIO.read(archivo);
            Tag tag = audioFile.getTag();
            AudioHeader header = audioFile.getAudioHeader();

            String titulo = (tag != null && !tag.getFirst(FieldKey.TITLE).isEmpty())
                            ? tag.getFirst(FieldKey.TITLE)
                            : archivo.getName();

            String artista = (tag != null && !tag.getFirst(FieldKey.ARTIST).isEmpty())
                            ? tag.getFirst(FieldKey.ARTIST)
                            : "Desconocido";


            String genero = (tag != null && !tag.getFirst(FieldKey.GENRE).isEmpty())
                            ? tag.getFirst(FieldKey.GENRE)
                            : "Desconocido";


            String anio = (tag != null && !tag.getFirst(FieldKey.YEAR).isEmpty())
                            ? tag.getFirst(FieldKey.YEAR)
                            : "Desconocido";

            int duracion = (header != null) ? header.getTrackLength() : 0; // en segundos

            // ✅ Nuevo parámetro: nombre real del archivo (sin la ruta)
            String nombreArchivo = archivo.getName();

            return new Cancion(
                    java.util.UUID.randomUUID().toString(),
                    titulo,
                    artista,
                    genero,
                    anio,
                    duracion,
                    nombreArchivo  // 🔹 Se pasa al constructor
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
