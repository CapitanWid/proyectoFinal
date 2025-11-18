import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:just_audio/just_audio.dart';
import 'package:syncup_music_app/constants/constants.dart';
import 'package:syncup_music_app/model/cancion.dart';

class AudioPlayerService {
  static final AudioPlayerService _instance = AudioPlayerService._internal();
  factory AudioPlayerService() => _instance;
  AudioPlayerService._internal();

  final AudioPlayer player = AudioPlayer();

  // --- Estado global ---
  final ValueNotifier<Cancion?> currentSong = ValueNotifier(null);
  final ValueNotifier<bool> isPlaying = ValueNotifier(false);
  final ValueNotifier<bool> isLoading = ValueNotifier(false);

  // --- Cola de reproduccion ---
  List<Cancion> playQueue = [];
  int currentIndex = 0;

  // --- Favoritos (canciones por id) ---
  final List<String> favoriteSongIds = [];

  String audioUrl = "$baseUrl/api/canciones/";
  String usuarioId = ""; // Se define al iniciar sesión

  // --- Reproducir una canción ---
  Future<void> playSong(Cancion cancion) async {
    try {
      isLoading.value = true;
      currentSong.value = cancion;

      await player.setUrl('$audioUrl${cancion.nombreArchivo}');
      await player.play();

      isPlaying.value = true;
    } catch (e) {
      debugPrint("Error al reproducir: $e");
    } finally {
      isLoading.value = false;
    }
  }

  void cargarCola(List<Cancion> canciones) {
    playQueue = canciones;
    currentIndex = 0;

    if (canciones.isNotEmpty) {
      playSong(canciones[0]);
    }
  }

  Future<void> reproducirEn(int index) async {
    if (index < 0 || index >= playQueue.length) return;

    currentIndex = index;
    await playSong(playQueue[index]);
  }

  void siguiente() {
    if (currentIndex < playQueue.length - 1) {
      currentIndex++;
      reproducirEn(currentIndex);
    }
  }


  Future<void> togglePlayPause() async {
    if (player.playing) {
      await player.pause();
      isPlaying.value = false;
    } else {
      await player.play();
      isPlaying.value = true;
    }
  }

  // --- Gestionar favoritos ---
  Future<void> toggleFavorite() async {
    final cancion = currentSong.value;
    if (cancion == null) return;
              

    final isFav = favoriteSongIds.contains(cancion.id);
    final uri = Uri.parse(
      '$baseUrl/api/favoritos/${isFav ? "eliminar" : "agregar"}?usuarioId=$usuarioId&cancionId=${cancion.id}',
    );

    try {
      if (isFav) {
        await http.delete(uri);
        favoriteSongIds.remove(cancion.id);
      } else {
        await http.post(uri);
        favoriteSongIds.add(cancion.id);
      }
      // Refresca el estado para el icono del corazón
      currentSong.notifyListeners();
    } catch (e) {
      debugPrint("Error al actualizar favorito: $e");
    }
  }

  bool isFavorite(Cancion cancion) => favoriteSongIds.contains(cancion.id);


  Future<void> iniciarRadio(Cancion base) async {
    final url = Uri.parse('$baseUrl/api/canciones/radio/${base.id}');
    final res = await http.get(url);

    if (res.statusCode == 200) {
      final data = jsonDecode(res.body) as List;
      final lista = data.map((e) => Cancion.fromJson(e)).toList();
      cargarCola(lista);
    } else {
      debugPrint("Error al iniciar radio: ${res.statusCode}");
    }
  }

  void reset() {
    currentSong.value = null;
    isPlaying.value = false;
    isLoading.value = false;

    playQueue.clear();
    currentIndex = 0;

    favoriteSongIds.clear();

    player.stop();
  }


}
