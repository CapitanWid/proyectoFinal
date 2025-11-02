package com.uniquindio.server.syncup.controller;

import com.uniquindio.server.syncup.dto.BusquedaRequest;
import com.uniquindio.server.syncup.dto.FiltroRequest;
import com.uniquindio.server.syncup.model.Cancion;
import com.uniquindio.server.syncup.service.RepositorioCanciones;
import com.uniquindio.server.syncup.service.ExtractorMetadatos;
import com.uniquindio.server.syncup.datastructures.ListaSimple;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/canciones")
public class CancionController {

    // Repositorio compartido (instancia única)
    private static final RepositorioCanciones repo = new RepositorioCanciones();

    public static RepositorioCanciones getRepositorio() {
        return repo;
    }

    // ============================================================
    // 1️⃣ OBTENER ARCHIVO DE AUDIO
    // ============================================================
    @GetMapping("/{nombreArchivo}")
    public ResponseEntity<Resource> obtenerCancion(@PathVariable String nombreArchivo) {
        File archivo = new File("D:\\datos_syncup\\songs\\", nombreArchivo);

        if (!archivo.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource recurso = new FileSystemResource(archivo);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + archivo.getName() + "\"")
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(recurso);
    }

    // ============================================================
    // 2️⃣ BUSCAR CANCIONES POR TÍTULO Y FILTROS (usuario)
    // ============================================================
    @PostMapping("/busqueda")
    public ResponseEntity<List<Cancion>> buscarCanciones(@RequestBody BusquedaRequest request) {
        ListaSimple<FiltroRequest> filtrosLista = new ListaSimple<>();
        if (request.getFiltros() != null) {
            for (FiltroRequest f : request.getFiltros()) {
                filtrosLista.agregarFinal(f);
            }
        }

        ListaSimple<Cancion> resultados = repo.buscarPorCriterios(
                request.getTitulo(),
                filtrosLista
        );

        List<Cancion> lista = new ArrayList<>();
        for (Cancion c : resultados) {
            lista.add(c);
        }

        return ResponseEntity.ok(lista);
    }

    // ============================================================
    // 3️⃣ AUTOCOMPLETAR TÍTULOS (usuario)
    // ============================================================
    @GetMapping("/autocompletar")
    public ResponseEntity<List<String>> autocompletarTitulos(@RequestParam String query) {
        List<String> sugerencias = new ArrayList<>();
        for (Cancion c : repo.getListaCanciones()) {
            if (c.getTitulo().toLowerCase().contains(query.toLowerCase()) && !sugerencias.contains(c.getTitulo())) {
                sugerencias.add(c.getTitulo());
            }
        }
        return ResponseEntity.ok(sugerencias);
    }

    // ============================================================
    // 4️⃣ LISTAR TODAS LAS CANCIONES (Administrador)
    // ============================================================
    @GetMapping
    public ResponseEntity<List<Cancion>> listarCanciones() {
        List<Cancion> lista = new ArrayList<>();
        for (Cancion c : repo.getListaCanciones()) {
            lista.add(c);
        }
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(lista);
    }

    // ============================================================
    // 5️⃣ ACTUALIZAR METADATOS DE UNA CANCIÓN (Administrador)
    // ============================================================
    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarCancion(@PathVariable String id,
                                                    @RequestBody Cancion nuevaData) {
        Cancion existente = null;
        for (Cancion c : repo.getListaCanciones()) {
            if (c.getId().equals(id)) {
                existente = c;
                break;
            }
        }

        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Canción no encontrada");
        }

        // ✅ Solo actualizar campos relevantes
        existente.setTitulo(nuevaData.getTitulo());
        existente.setArtista(nuevaData.getArtista());
        existente.setGenero(nuevaData.getGenero());
        existente.setAnio(nuevaData.getAnio());
        existente.setNombreArchivo(nuevaData.getNombreArchivo());

        return ResponseEntity.ok("Metadatos actualizados correctamente");
    }

    // ============================================================
    // 6️⃣ ELIMINAR UNA CANCIÓN (solo de memoria)
    // ============================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCancion(@PathVariable String id) {
        boolean eliminado = repo.eliminarCancionPorId(id);

        if (eliminado) {
            return ResponseEntity.ok("Canción eliminada correctamente de la memoria");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró la canción con ID: " + id);
        }
    }

    // ============================================================
    // 7️⃣ CARGAR CANCIONES MASIVAMENTE DESDE /no_listadas
    // ============================================================
    @PostMapping("/cargar")
    public ResponseEntity<String> cargarCancionesDesdeArchivo() {
        String rutaArchivo = "D:\\datos_syncup\\no_listadas\\metadatos.txt";
        String carpetaNoListadas = "D:\\datos_syncup\\no_listadas\\";

        try {
            List<String> lineas = Files.readAllLines(Paths.get(rutaArchivo));
            int cargadas = 0;

            for (String linea : lineas) {
                if (linea.trim().isEmpty() || linea.startsWith("titulo")) continue;

                String[] partes = linea.split(";");
                if (partes.length < 5) continue;

                String titulo = partes[0].trim();
                String artista = partes[1].trim();
                String genero = partes[2].trim();
                String anio = partes[3].trim();
                String nombreArchivo = partes[4].trim();

                File archivo = new File(carpetaNoListadas + nombreArchivo);
                Cancion c;

                if (archivo.exists()) {
                    c = ExtractorMetadatos.extraerDesdeArchivo(archivo);
                    if (c != null) {
                        repo.agregarCancion(c);
                        cargadas++;
                    }
                } else {
                    c = new Cancion(UUID.randomUUID().toString(), titulo, artista, 
                            "", genero, "", anio, 0, nombreArchivo);
                    repo.agregarCancion(c);
                    cargadas++;
                }
            }

            return ResponseEntity.ok("Se cargaron " + cargadas + " canciones desde la carpeta no_listadas");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al leer metadatos.txt: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }

    // ============================================================
    // 8️⃣ AGREGAR UNA CANCIÓN INDIVIDUAL (Administrador)
    // ============================================================
    @PostMapping
    public ResponseEntity<String> agregarCancion(@RequestBody Cancion nueva) {
        if (nueva == null || nueva.getTitulo() == null || nueva.getTitulo().isBlank()) {
            return ResponseEntity.badRequest().body("Datos inválidos o incompletos");
        }

        // Asignar ID si no viene en la solicitud
        if (nueva.getId() == null || nueva.getId().isBlank()) {
            nueva.setId(UUID.randomUUID().toString());
        }

        // Agregar a la lista del repositorio
        repo.agregarCancion(nueva);

        return ResponseEntity.ok("Canción agregada correctamente al catálogo");
    }

}
