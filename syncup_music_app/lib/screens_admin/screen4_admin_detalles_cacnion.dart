import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:syncup_music_app/constants/constants.dart';

class ScreenAdminDetalleCancion extends StatefulWidget {
  final Map<String, dynamic> cancion;

  const ScreenAdminDetalleCancion({super.key, required this.cancion});

  @override
  State<ScreenAdminDetalleCancion> createState() => _ScreenAdminDetalleCancionState();
}

class _ScreenAdminDetalleCancionState extends State<ScreenAdminDetalleCancion> {
  late TextEditingController tituloCtrl;
  late TextEditingController artistaCtrl;
  late TextEditingController generoCtrl;
  late TextEditingController anioCtrl;
  late TextEditingController archivoCtrl;

  @override
  void initState() {
    super.initState();
    final c = widget.cancion;
    tituloCtrl = TextEditingController(text: c['titulo']);
    artistaCtrl = TextEditingController(text: c['artista']);
    generoCtrl = TextEditingController(text: c['genero']);
    anioCtrl = TextEditingController(text: c['anio']);
    archivoCtrl = TextEditingController(text: c['nombreArchivo']);
  }

  // =======================================================
  // 🔹 ACTUALIZAR CANCIÓN (solo campos modificables)
  // =======================================================
  Future<void> _actualizarCancion() async {
    final id = widget.cancion['id'];
    final url = Uri.parse('$baseUrl/api/canciones/$id');

    final body = json.encode({
      "id": id,
      "titulo": tituloCtrl.text.trim(),
      "artista": artistaCtrl.text.trim(),
      "genero": generoCtrl.text.trim(),
      "anio": anioCtrl.text.trim(),
      "nombreArchivo": archivoCtrl.text.trim(),
    });

    final response = await http.put(url,
        headers: {'Content-Type': 'application/json'}, body: body);

    if (response.statusCode == 200) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Canción actualizada correctamente')),
      );
      Navigator.pop(context);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error al actualizar: ${response.body}')),
      );
    }
  }

  // =======================================================
  // 🔹 ELIMINAR CANCIÓN (con confirmación)
  // =======================================================
  Future<void> _eliminarCancion() async {
    final id = widget.cancion['id'];
    final url = Uri.parse('$baseUrl/api/canciones/$id');

    final confirmar = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Confirmar eliminación'),
        content: const Text('¿Deseas eliminar esta canción de la memoria?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: const Text('Cancelar'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(ctx, true),
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
            child: const Text('Eliminar'),
          ),
        ],
      ),
    );

    if (confirmar != true) return;

    final response = await http.delete(url);
    if (response.statusCode == 200) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Canción eliminada correctamente')),
      );
      Navigator.pop(context);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error al eliminar: ${response.body}')),
      );
    }
  }

  // =======================================================
  // 🔹 INTERFAZ
  // =======================================================
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          widget.cancion['titulo'] ?? 'Detalles de Canción',
          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
        ),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: ListView(
          children: [
            TextField(
              controller: tituloCtrl,
              decoration: const InputDecoration(labelText: 'Título'),
            ),
            TextField(
              controller: artistaCtrl,
              decoration: const InputDecoration(labelText: 'Artista'),
            ),
            TextField(
              controller: generoCtrl,
              decoration: const InputDecoration(labelText: 'Género'),
            ),
            TextField(
              controller: anioCtrl,
              decoration: const InputDecoration(labelText: 'Año'),
            ),
            TextField(
              controller: archivoCtrl,
              readOnly: true,
              decoration: const InputDecoration(labelText: 'Nombre del archivo'),
            ),
            const SizedBox(height: 30),
            ElevatedButton(
              onPressed: _actualizarCancion,
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.deepPurple.shade200,
                foregroundColor: Colors.black,
                padding: const EdgeInsets.symmetric(vertical: 16),
              ),
              child: const Text('Guardar cambios'),
            ),
            const SizedBox(height: 10),
            ElevatedButton(
              onPressed: _eliminarCancion,
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.red,
                foregroundColor: Colors.black,
                padding: const EdgeInsets.symmetric(vertical: 16),
              ),
              child: const Text('Eliminar canción'),
            ),
          ],
        ),
      ),
    );
  }
}
