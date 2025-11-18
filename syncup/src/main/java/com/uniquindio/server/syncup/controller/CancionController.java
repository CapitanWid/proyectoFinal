package com.uniquindio.server.syncup.controller;

import com.uniquindio.server.syncup.dto.BusquedaRequest;
import com.uniquindio.server.syncup.dto.FiltroRequest;
import com.uniquindio.server.syncup.model.Cancion;
import com.uniquindio.server.syncup.model.Usuario;
import com.uniquindio.server.syncup.service.RepositorioCanciones;
import com.uniquindio.server.syncup.service.ExtractorMetadatos;
import com.uniquindio.server.syncup.datastructures.GrafoDeSimilitudCancion;
import com.uniquindio.server.syncup.datastructures.ListaSimple;
import com.uniquindio.server.syncup.datastructures.TablaHashUsuarios;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/api/canciones")
public class CancionController {

    // Repositorio compartido (instancia única)
    private static final RepositorioCanciones repo = new RepositorioCanciones();

    // Grafo de similitud (se construye una sola vez)
    private static GrafoDeSimilitudCancion grafoSimilitud = null;


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
    
    //public ResponseEntity<List<Cancion>> buscarCanciones(@RequestBody BusquedaRequest request) {
    @PostMapping("/busqueda")
    public ResponseEntity<Cancion[]> buscarCanciones(@RequestBody BusquedaRequest request){

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

        //List<Cancion> lista = new ArrayList<>();
        ListaSimple<Cancion> lista = new ListaSimple<>();
        for (Cancion c : resultados) {
            //lista.add(c);
            lista.agregarFinal(c);
        }

       // return ResponseEntity.ok(lista.toArray());
        return ResponseEntity.ok(lista.toArray(Cancion.class));


    }

    @GetMapping("/autocompletar")
    public ResponseEntity<String[]> autocompletarTitulos(@RequestParam String query) {

        ListaSimple<String> sugerencias = repo.autocompletarTitulos(query);

        String[] arr = sugerencias.toArray(String.class);

        return ResponseEntity.ok(arr);
    }


    // ============================================================
    // 4️⃣ LISTAR TODAS LAS CANCIONES (Administrador)
    // ============================================================

    @GetMapping
    public ResponseEntity<Cancion[]> listarCanciones() {

        // 1. Obtener la lista propia
        ListaSimple<Cancion> lista = repo.getListaCanciones();

        // 2. Si está vacía → NO_CONTENT
        if (lista.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // 3. Convertir ListaSimple → Cancion[]
        Cancion[] arreglo = lista.toArray(Cancion.class);

        // 4. Retornar arreglo JSON
        return ResponseEntity.ok(arreglo);
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
    public ResponseEntity<String> cargarCancionesDesdeArchivo(@RequestBody Map<String, String> body) {

        String archivoSeleccionado = body.get("archivo");  // <<--- VIENE DESDE FLUTTER
        if (archivoSeleccionado == null || archivoSeleccionado.isEmpty()) {
            return ResponseEntity.badRequest().body("No se recibio el nombre del archivo a cargar.");
        }

        String carpetaNoListadas = "D:\\datos_syncup\\no_listadas\\";
        String rutaArchivo = carpetaNoListadas + archivoSeleccionado;  // <<--- ARCHIVO DINÁMICO

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {

            String linea;
            int cargadas = 0;

            while ((linea = br.readLine()) != null) {

                if (linea.trim().isEmpty() || linea.startsWith("titulo")) {
                    continue;
                }

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
                    c = new Cancion(
                            UUID.randomUUID().toString(),
                            titulo,
                            artista,
                            genero,
                            anio,
                            0,
                            nombreArchivo
                    );
                    repo.agregarCancion(c);
                    cargadas++;
                }
            }

            return ResponseEntity.ok("Se cargaron " + cargadas + 
                " canciones desde el archivo: " + archivoSeleccionado);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al leer " + archivoSeleccionado + ": " + e.getMessage());
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

    // ============================================================
    // 9️⃣ LISTAR ARCHIVOS EN /no_listadas
    // ============================================================


    @GetMapping("/archivos")
    public ResponseEntity<String[]> listarArchivosNoListadas() {
        try {
            File carpeta = new File("D:\\datos_syncup\\no_listadas\\");
            if (!carpeta.exists() || !carpeta.isDirectory()) {
                String[] error = { "No se encontro la carpeta especificada" };
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            String[] nombres = carpeta.list();
            if (nombres == null || nombres.length == 0) {
                String[] vacio = {};
                return ResponseEntity.ok(vacio);
            }

            return ResponseEntity.ok(nombres);

        } catch (Exception e) {
            String[] error = { "Error al listar archivos: " + e.getMessage() };
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    // ============================================================
    // 🔄 Construir o reconstruir grafo de similitud
    // ============================================================

    private void construirGrafoSiNoExiste() {
        if (grafoSimilitud == null) {

            // 1. Obtener tu ListaSimple
            ListaSimple<Cancion> lista = repo.getListaCanciones();

            // 2. Convertir a arreglo propio
            Cancion[] cancionesArr = lista.toArray(Cancion.class);

            // 3. Construir el grafo usando solo tu arreglo
            grafoSimilitud = GrafoDeSimilitudCancion.construir(cancionesArr, 1);

            System.out.println("Grafo de similitud construido con " + cancionesArr.length + " canciones.");
        }
    }


    // ============================================================
    // 🔟 RECOMENDAR CANCIONES SIMILARES // PARA INICIAR RADIO
    // ============================================================
    /* 
    @GetMapping("/radio/{idCancion}")
    public ResponseEntity<List<Cancion>> iniciarRadio(@PathVariable String idCancion) {
        try {
            construirGrafoSiNoExiste();

            // Buscar la cancion base por ID
            Cancion base = repo.buscarPorId(idCancion);
            if (base == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
            }

            // Obtener 10 similares
            List<Cancion> radio = grafoSimilitud.recomendarCanciones(base, 10);

            return ResponseEntity.ok(radio);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
*/
   @GetMapping("/radio/{idCancion}")
    public ResponseEntity<Cancion[]> iniciarRadio(@PathVariable String idCancion) {
        try {
            construirGrafoSiNoExiste();

            // 1. Buscar la canción base por ID
            Cancion base = repo.buscarPorId(idCancion);
            if (base == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Cancion[0]);
            }

            // 2. Obtener recomendación usando SOLO estructuras propias
            ListaSimple<Cancion> radioLista =
                    grafoSimilitud.recomendarCancionesPropio(base, 10);

            // 3. Convertir ListaSimple → arreglo JSON-friendly
            Cancion[] respuesta = radioLista.toArray(Cancion.class);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Cancion[0]);
        }
    }

    @PostMapping("/recomendar-por-favoritos")
    public ResponseEntity<Cancion[]> recomendarPorFavoritos(@RequestParam String nombreUsuario) {
        try {
            TablaHashUsuarios tablaUsuarios = FormularioController.getTablaUsuarios();
            Usuario usuario = tablaUsuarios.buscarUsuario(nombreUsuario);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Cancion[0]);
            }

            // Favoritos ahora es ListaSimple
            ListaSimple<Cancion> favoritas = usuario.getListaFavoritos();

            if (favoritas == null || favoritas.size() == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new Cancion[0]);
            }

            // 1. catálogo propio
            ListaSimple<Cancion> lista = repo.getListaCanciones();
            Cancion[] arregloCatalogo = lista.toArray(Cancion.class);

            // 2. construir grafo
            GrafoDeSimilitudCancion grafoGeneral =
                    GrafoDeSimilitudCancion.construir(arregloCatalogo, 1);

            // 3. recomendaciones propias
            ListaSimple<Cancion> recomendadas = new ListaSimple<>();

            for (Cancion fav : favoritas) {

                ListaSimple<Cancion> similaresLista =
                        grafoGeneral.recomendarCancionesPropio(fav, 5);

                for (Cancion s : similaresLista) {

                    boolean yaEsFavorita = favoritas.obtenerPosicionNodo(s) != -1;
                    boolean yaAgregada = recomendadas.obtenerPosicionNodo(s) != -1;

                    if (!yaEsFavorita && !yaAgregada) {
                        recomendadas.agregarFinal(s);
                    }
                }
            }

            if (recomendadas.size() == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new Cancion[0]);
            }

            Cancion[] respuesta = recomendadas.toArray(Cancion.class);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Cancion[0]);
        }
    }












}
