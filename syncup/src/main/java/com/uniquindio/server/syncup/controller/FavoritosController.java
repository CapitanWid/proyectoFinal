package com.uniquindio.server.syncup.controller;

import com.uniquindio.server.syncup.datastructures.TablaHashUsuarios;
import com.uniquindio.server.syncup.model.Cancion;
import com.uniquindio.server.syncup.model.Usuario;
import com.uniquindio.server.syncup.service.RepositorioCanciones;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

import java.util.LinkedList;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritosController {

    private final TablaHashUsuarios tablaUsuarios;
    private final RepositorioCanciones repoCanciones; // 🔹 Acceso real al repositorio

    public FavoritosController() {
        this.tablaUsuarios = FormularioController.getTablaUsuarios();
        this.repoCanciones = CancionController.getRepositorio(); 

    }

    @PostMapping("/agregar")
    public String agregarFavorito(@RequestParam String usuarioId, @RequestParam String cancionId) {
       
               
        Usuario usuario = tablaUsuarios.buscarUsuario(usuarioId);
        if (usuario == null) {
            return "Usuario no encontrado";
        }

        Cancion cancion = buscarCancionPorId(cancionId);
        if (cancion == null) {
            return "Cancion no encontrada";
        }

        usuario.agregarFavorito(cancion);
        System.out.println("✅ Favorito agregado: " + cancion.getTitulo() + " para " + usuarioId);
        return "Cancion agregada a favoritos";
    }

    @DeleteMapping("/eliminar")
    public String eliminarFavorito(@RequestParam String usuarioId, @RequestParam String cancionId) {
        Usuario usuario = tablaUsuarios.buscarUsuario(usuarioId);
        if (usuario == null) {
            return "Usuario no encontrado";
        }

        Cancion cancion = buscarCancionPorId(cancionId);
        if (cancion == null) {
            return "Cancion no encontrada";
        }

        usuario.eliminarFavorito(cancion);
        System.out.println("🗑️ Favorito eliminado: " + cancion.getTitulo() + " para " + usuarioId);
        return "Cancion eliminada de favoritos";
    }

    @GetMapping("/{usuarioId}")
    public LinkedList<Cancion> obtenerFavoritos(@PathVariable String usuarioId) {
        Usuario usuario = tablaUsuarios.buscarUsuario(usuarioId);
        if (usuario == null) return new LinkedList<>();
        return usuario.getListaFavoritos();
    }

    private Cancion buscarCancionPorId(String id) {
        for (Cancion c : repoCanciones.getListaCanciones()) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }
    
    @GetMapping("/{usuarioId}/csv")
    public ResponseEntity<Resource> generarCsvFavoritos(@PathVariable String usuarioId) {

        Usuario usuario = tablaUsuarios.buscarUsuario(usuarioId);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        LinkedList<Cancion> favoritos = usuario.getListaFavoritos();
        if (favoritos == null || favoritos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // Construir CSV
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("titulo;artista;genero;anio;archivo\n");

        for (Cancion c : favoritos) {
            csvBuilder.append(c.getTitulo()).append(";")
                    .append(c.getArtista()).append(";")
                    .append(c.getGenero()).append(";")
                    .append(c.getAnio()).append(";")
                    .append(c.getNombreArchivo()).append("\n");
        }

        try {
            // Crear archivo temporal
            Path tempFile = Files.createTempFile("favoritos_", ".csv");
            Files.write(tempFile, csvBuilder.toString().getBytes());

            Resource resource = new FileSystemResource(tempFile.toFile());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=favoritos.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




}
