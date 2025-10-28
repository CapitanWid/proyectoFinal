package com.uniquindio.server.syncup.controller;

import com.uniquindio.server.syncup.dto.BusquedaRequest;
import com.uniquindio.server.syncup.model.Cancion;
import com.uniquindio.server.syncup.service.RepositorioCanciones;
import com.uniquindio.server.syncup.datastructures.ListaSimple;
import com.uniquindio.server.syncup.dto.FiltroRequest;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/canciones")
public class CancionController {

    // 🔹 Repositorio compartido (una sola instancia para toda la app)
    private static final RepositorioCanciones repo = new RepositorioCanciones();

    // 🔹 Método público para acceder al repositorio desde otros controladores
    public static RepositorioCanciones getRepositorio() {
        return repo;
    }

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
}
