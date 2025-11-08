  import 'dart:convert';
  import 'package:flutter/material.dart';
  import 'package:http/http.dart' as http;
  import 'package:syncup_music_app/constants/constants.dart';
  import 'package:syncup_music_app/screens_admin/screen4_admin_detalles_cacnion.dart';
  import 'package:syncup_music_app/screens_admin/screen5_admin_agregar_cancion.dart';

  class ScreenAdminGestionarCanciones extends StatefulWidget {
    const ScreenAdminGestionarCanciones({super.key});

    @override
    State<ScreenAdminGestionarCanciones> createState() =>
        _ScreenAdminGestionarCancionesState();
  }

  class _ScreenAdminGestionarCancionesState
      extends State<ScreenAdminGestionarCanciones> {
    List<dynamic> canciones = [];
    bool cargando = true;

    @override
    void initState() {
      super.initState();
      _cargarCanciones();
    }

    Future<void> _cargarCanciones() async {
      try {
        final url = Uri.parse('$baseUrl/api/canciones');
        final response = await http.get(url);

        if (response.statusCode == 200) {
          setState(() {
            canciones = json.decode(utf8.decode(response.bodyBytes));
            cargando = false;
          });
        } else {
          setState(() => cargando = false);
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Error al cargar canciones (${response.statusCode})')),
          );
        }
      } catch (e) {
        setState(() => cargando = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error de conexión: $e')),
        );
      }
    }

    // =========================================================
    // 🔹 Tarjeta superior para agregar nueva canción
    // =========================================================
    Widget _buildAgregarCancionCard() {
      return Card(
        margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        elevation: 3,
        color: Colors.deepPurple.shade100, // color distintivo
        child: ListTile(
          title: const Text(
            'AGREGAR CANCIÓN',
            style: TextStyle(fontWeight: FontWeight.bold),
          ),
          subtitle: const Text('Ingresar metadatos'),
          trailing: ElevatedButton(
            onPressed: () async {
              await Navigator.push(
                context,
                MaterialPageRoute(builder: (_) => const ScreenAdminAgregarCancion()),
              );
              _cargarCanciones(); // refrescar lista al volver
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.blue.shade300,
              foregroundColor: Colors.black,
            ),
            child: const Text('Nueva'),
          ),
        ),
      );
    }

    // =========================================================
    // 🔹 Tarjeta normal de canción existente
    // =========================================================
    Widget _buildCancionCard(Map<String, dynamic> cancion) {
      return Card(
        margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        elevation: 3,
        child: ListTile(
          title: Text(
            cancion['titulo'] ?? 'Sin título',
            style: const TextStyle(fontWeight: FontWeight.bold),
          ),
          subtitle: Text(cancion['artista'] ?? 'Artista desconocido'),
          trailing: ElevatedButton(
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (_) => ScreenAdminDetalleCancion(cancion: cancion),
                ),
              ).then((_) => _cargarCanciones());
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.deepPurple.shade200,
              foregroundColor: Colors.black,
            ),
            child: const Text('Gestionar'),
          ),
        ),
      );
    }

    // =========================================================
    // 🔹 BUILD
    // =========================================================
    @override
    Widget build(BuildContext context) {
      return Scaffold(
        appBar: AppBar(
          title: const Text('Gestionar Canciones', style: TextStyle(fontSize: 20)),
        ),
        body: cargando
            ? const Center(child: CircularProgressIndicator())
            : canciones.isEmpty
                ? const Center(child: Text('No hay canciones cargadas'))
                : ListView(
                    children: [
                      _buildAgregarCancionCard(), // aparece siempre primero
                      ...canciones.map((c) => _buildCancionCard(c)).toList(),
                    ],
                  ),
      );
    }
  }
