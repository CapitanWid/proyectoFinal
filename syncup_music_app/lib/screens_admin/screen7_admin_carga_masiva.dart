import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:syncup_music_app/constants/constants.dart';

class ScreenAdminCargarCanciones extends StatefulWidget {
  const ScreenAdminCargarCanciones({super.key});

  @override
  State<ScreenAdminCargarCanciones> createState() =>
      _ScreenAdminCargarCancionesState();
}

class _ScreenAdminCargarCancionesState
    extends State<ScreenAdminCargarCanciones> {
  bool cargando = false;
  String resultado = "";
  String rutaActual = "D:\\datos_syncup\\no_listadas\\";
  List<String> archivos = [];
  String? archivoSeleccionado;

  // 🔥 AHORA ES UNA LISTA (LA SOLUCIÓN REAL)
  List<String> archivosCargados = [];

  @override
  void initState() {
    super.initState();
    _listarArchivos();
  }

  Future<void> _listarArchivos() async {
    setState(() => cargando = true);

    final url = Uri.parse('$baseUrl/api/canciones/archivos');
    try {
      final response = await http.get(url);
      if (response.statusCode == 200) {
        final lista = List<String>.from(json.decode(response.body));
        setState(() => archivos = lista);
      } else {
        ScaffoldMessenger.of(context)
            .showSnackBar(SnackBar(content: Text('Error al listar archivos: ${response.body}')));
      }
    } catch (e) {
      ScaffoldMessenger.of(context)
          .showSnackBar(SnackBar(content: Text('Error de conexión: $e')));
    } finally {
      setState(() => cargando = false);
    }
  }

  Future<void> _cargarCanciones() async {
    if (archivoSeleccionado == null) return;

    setState(() {
      cargando = true;
      resultado = "";
    });

    final url = Uri.parse('$baseUrl/api/canciones/cargar');

    try {
      final response = await http.post(
        url,
        headers: {"Content-Type": "application/json"},
        body: jsonEncode({"archivo": archivoSeleccionado}),
      );

      if (response.statusCode == 200) {
        setState(() {
          resultado = response.body;

          // 🔥 AGREGAR EL ARCHIVO A LA LISTA DE BLOQUEADOS
          if (!archivosCargados.contains(archivoSeleccionado)) {
            archivosCargados.add(archivoSeleccionado!);
          }
        });

        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Canciones cargadas correctamente ✅')),
        );
      } else {
        setState(() => resultado = "Error: ${response.body}");
        ScaffoldMessenger.of(context)
            .showSnackBar(SnackBar(content: Text('Error: ${response.body}')));
      }
    } catch (e) {
      setState(() => resultado = "Error de conexión: $e");
    } finally {
      setState(() => cargando = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final textStyle = const TextStyle(fontFamily: 'monospace', fontSize: 14);

    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'Explorador de archivos - Carga masiva',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _listarArchivos,
            tooltip: 'Actualizar lista',
          ),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Ruta actual
            Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
              decoration: BoxDecoration(
                color: Colors.deepPurple.shade50,
                border: Border.all(color: Colors.deepPurple.shade100),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Text(
                rutaActual,
                style: textStyle,
                overflow: TextOverflow.ellipsis,
              ),
            ),
            const SizedBox(height: 12),

            // Lista de archivos
            Expanded(
              child: cargando
                  ? const Center(child: CircularProgressIndicator())
                  : archivos.isEmpty
                      ? const Center(child: Text("No se encontraron archivos"))
                      : ListView.builder(
                          itemCount: archivos.length,
                          itemBuilder: (context, index) {
                            final archivo = archivos[index];

                            final esSeleccionado =
                                archivo == archivoSeleccionado;

                            final yaCargado =
                                archivosCargados.contains(archivo);

                            return ListTile(
                              tileColor: yaCargado
                                  ? Colors.grey.shade300
                                  : esSeleccionado
                                      ? Colors.deepPurple.shade100
                                      : null,

                              leading: Icon(
                                Icons.description,
                                color: yaCargado
                                    ? Colors.grey // 🔥 icono gris
                                    : Colors.amber,
                              ),

                              title: Text(
                                archivo,
                                style: TextStyle(
                                  fontFamily: 'monospace',
                                  fontSize: 14,
                                  color: yaCargado
                                      ? Colors.grey // 🔥 texto gris
                                      : Colors.black,
                                ),
                              ),

                              onTap: yaCargado
                                  ? null
                                  : () => setState(
                                      () => archivoSeleccionado = archivo),
                            );
                          },
                        ),
            ),

            const SizedBox(height: 20),

            // Botón de carga
            Center(
              child: ElevatedButton.icon(
                onPressed: cargando ||
                        archivoSeleccionado == null ||
                        archivosCargados.contains(archivoSeleccionado)
                    ? null
                    : _cargarCanciones,
                icon: const Icon(Icons.upload_file),
                label: Text(
                  archivoSeleccionado == null
                      ? 'Selecciona un archivo para cargar'
                      : 'Cargar $archivoSeleccionado',
                  style: const TextStyle(fontSize: 15),
                ),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.deepPurple.shade200,
                  foregroundColor: Colors.black,
                  padding:
                      const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
                ),
              ),
            ),

            const SizedBox(height: 16),

            if (resultado.isNotEmpty)
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(14),
                decoration: BoxDecoration(
                  color: Colors.green.shade50,
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: Colors.green.shade100),
                ),
                child: Text(resultado, style: const TextStyle(fontSize: 14)),
              ),
          ],
        ),
      ),
    );
  }
}
