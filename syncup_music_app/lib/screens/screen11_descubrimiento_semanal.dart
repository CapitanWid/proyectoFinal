import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:syncup_music_app/constants/constants.dart';
import 'package:syncup_music_app/model/cancion.dart';
import 'package:syncup_music_app/service/audio_player_service.dart';
import 'package:syncup_music_app/widgets/audio_player_widget.dart';

class ScreenDescubrimientoSemanal extends StatefulWidget {
  const ScreenDescubrimientoSemanal({super.key});

  @override
  State<ScreenDescubrimientoSemanal> createState() =>
      _ScreenDescubrimientoSemanalState();
}

class _ScreenDescubrimientoSemanalState
    extends State<ScreenDescubrimientoSemanal> {
  final AudioPlayerService _audioService = AudioPlayerService();

  bool _isLoading = true;
  List<Cancion> _recomendaciones = [];

  @override
  void initState() {
    super.initState();
    _cargarRecomendaciones();
  }

  // ============================================================
  // Llama al backend para obtener recomendaciones
  // ============================================================
  Future<void> _cargarRecomendaciones() async {
    if (userUsuario.isEmpty) {
      debugPrint("⚠️ No se ha definido el nombre del usuario");
      setState(() => _isLoading = false);
      return;
    }

    setState(() => _isLoading = true);

    try {
      final url = Uri.parse(
          '$baseUrl/api/canciones/recomendar-por-favoritos?nombreUsuario=$userUsuario');

      final response = await http.post(url);

      if (response.statusCode == 200) {
        final List<dynamic> jsonList = jsonDecode(response.body);
        setState(() {
          _recomendaciones =
              jsonList.map((json) => Cancion.fromJson(json)).toList();
        });
      } else if (response.statusCode == 204) {
        setState(() {
          _recomendaciones = [];
        });
      } else {
        throw Exception("Error de servidor: ${response.statusCode}");
      }
    } catch (e) {
      debugPrint("Error al obtener recomendaciones: $e");
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Error al cargar recomendaciones")),
        );
      }
    } finally {
      setState(() => _isLoading = false);
    }
  }

  // ============================================================
  // Construcción del widget
  // ============================================================
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Descubrimiento Semanal"),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _cargarRecomendaciones,
          ),
        ],
      ),
      body: Column(
        children: [
          Expanded(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: _isLoading
                  ? const Center(child: CircularProgressIndicator())
                  : _recomendaciones.isEmpty
                      ? const Center(
                          child: Text(
                            "No hay recomendaciones disponibles aún 😔",
                            style: TextStyle(fontSize: 16),
                            textAlign: TextAlign.center,
                          ),
                        )
                      : ListView.separated(
                          itemCount: _recomendaciones.length,
                          separatorBuilder: (_, __) =>
                              const Divider(height: 10),
                          itemBuilder: (context, index) {
                            final cancion = _recomendaciones[index];
                            return ListTile(
                              leading: const Icon(Icons.music_note,
                                  color: Colors.deepPurple),
                              title: Text(
                                cancion.titulo,
                                style: const TextStyle(
                                    fontWeight: FontWeight.bold),
                              ),
                              subtitle: Text(
                                "${cancion.artista} • ${cancion.genero} • ${cancion.anio}",
                                style: const TextStyle(fontSize: 13),
                              ),
                              onTap: () => _audioService.playSong(cancion),
                            );
                          },
                        ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            child: AudioPlayerWidget(),
          ),
        ],
      ),
    );
  }
}
