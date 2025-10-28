import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:syncup_music_app/constants/constants.dart';
import 'package:syncup_music_app/model/cancion.dart';
import 'package:syncup_music_app/service/audio_player_service.dart';
import 'package:syncup_music_app/widgets/audio_player_widget.dart';

class FavoritosScreen extends StatefulWidget {
  final String usuarioId;

  const FavoritosScreen({super.key, required this.usuarioId});

  @override
  State<FavoritosScreen> createState() => _FavoritosScreenState();
}

class _FavoritosScreenState extends State<FavoritosScreen> {
  final AudioPlayerService _audioService = AudioPlayerService();
  List<Cancion> _favoritos = [];
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _cargarFavoritos();
  }

  Future<void> _cargarFavoritos() async {
    setState(() => _isLoading = true);
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/api/favoritos/${widget.usuarioId}'),
      );
      if (response.statusCode == 200) {
        final List<dynamic> listaJson = jsonDecode(response.body);
        setState(() {
          _favoritos = listaJson.map((json) => Cancion.fromJson(json)).toList();
        });
      } else {
        throw Exception("Error al obtener favoritos");
      }
    } catch (e) {
      debugPrint("Error cargando favoritos: $e");
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _eliminarFavorito(String cancionId) async {
    try {
      final response = await http.delete(
        Uri.parse('$baseUrl/api/favoritos/eliminar?usuarioId=${widget.usuarioId}&cancionId=$cancionId'),
      );
      if (response.statusCode == 200) {
        setState(() {
          _favoritos.removeWhere((c) => c.id == cancionId);
        });
      } else {
        throw Exception("Error al eliminar favorito");
      }
    } catch (e) {
      debugPrint("Error eliminando favorito: $e");
    }
  }

  void _iniciarRadio(String cancionId) {
    // Este método podría abrir otra pantalla o pedir al backend las canciones similares
    debugPrint("Iniciando radio para canción $cancionId");
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Mis Favoritos")),
      body: Column(
        children: [
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator())
                : _favoritos.isEmpty
                    ? const Center(child: Text("No tienes canciones favoritas"))
                    : ListView.builder(
                        itemCount: _favoritos.length,
                        itemBuilder: (context, index) {
                          final cancion = _favoritos[index];
                          return ListTile(
                            leading: IconButton(
                              icon: const Icon(Icons.play_circle_fill),
                              onPressed: () {
                                _audioService.playSong(
                                    cancion);
                              },
                            ),
                            title: Text(
                              cancion.titulo,
                              style:
                                  const TextStyle(fontWeight: FontWeight.bold),
                            ),
                            subtitle: Text(cancion.artista),
                            trailing: IconButton(
                              icon: const Icon(Icons.favorite, color: Colors.red),
                              onPressed: () {
                                _eliminarFavorito(cancion.id);
                              },
                            ),
                            onTap: () {
                              _audioService.playSong(
                                  cancion);
                            },
                          );
                        },
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
