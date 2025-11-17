import 'package:flutter/material.dart';
import 'package:syncup_music_app/service/audio_player_service.dart';
import 'package:just_audio/just_audio.dart';
import 'package:syncup_music_app/model/cancion.dart';

class AudioPlayerWidget extends StatelessWidget {
  const AudioPlayerWidget({super.key});

  @override
  Widget build(BuildContext context) {
    final audioService = AudioPlayerService();
    final player = audioService.player;

    return Container(
      height: 180,
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.1),
            blurRadius: 8,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          // ---------- PARTE SUPERIOR ----------
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Container(
                width: 60,
                height: 60,
                decoration: BoxDecoration(
                  color: Colors.grey.shade200,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: const Icon(
                  Icons.music_note,
                  color: Colors.deepPurple,
                  size: 38,
                ),
              ),
              const SizedBox(width: 12),

              // Información de la canción
              Expanded(
                child: ValueListenableBuilder<Cancion?>(
                  valueListenable: audioService.currentSong,
                  builder: (context, cancion, _) {
                    final bool hasSong = cancion != null;
                    return Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          hasSong
                              ? cancion!.titulo
                              : "Sin canción seleccionada",
                          overflow: TextOverflow.ellipsis,
                          style: const TextStyle(
                            color: Colors.black87,
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          hasSong ? cancion!.artista : "",
                          overflow: TextOverflow.ellipsis,
                          style: const TextStyle(
                            color: Colors.black54,
                            fontSize: 14,
                          ),
                        ),
                      ],
                    );
                  },
                ),
              ),
            ],
          ),

          const SizedBox(height: 1),

          // ---------- BARRA DE PROGRESO ----------
          StreamBuilder<Duration>(
            stream: player.positionStream,
            builder: (context, snapshot) {
              final position = snapshot.data ?? Duration.zero;
              final total = player.duration ?? Duration.zero;
              final progress = total.inMilliseconds == 0
                  ? 0.0
                  : position.inMilliseconds / total.inMilliseconds;

              return Column(
                children: [
                  LinearProgressIndicator(
                    value: progress,
                    backgroundColor: Colors.grey.shade300,
                    color: Colors.deepPurple,
                    minHeight: 5,
                    borderRadius: BorderRadius.circular(4),
                  ),
                  const SizedBox(height: 4),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(
                        _formatDuration(position),
                        style: const TextStyle(
                            color: Colors.black54, fontSize: 12),
                      ),
                      Text(
                        _formatDuration(total),
                        style: const TextStyle(
                            color: Colors.black54, fontSize: 12),
                      ),
                    ],
                  ),
                ],
              );
            },
          ),

          const SizedBox(height: 1),

          // ---------- CONTROLES ----------
          ValueListenableBuilder<Cancion?>(
            valueListenable: audioService.currentSong,
            builder: (context, cancion, _) {
              return Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  // ❤️ Favorito
                  IconButton(
                    icon: Icon(
                      cancion != null && audioService.isFavorite(cancion)
                          ? Icons.favorite
                          : Icons.favorite_border,
                      color: cancion != null &&
                              audioService.isFavorite(cancion)
                          ? Colors.red
                          : Colors.deepPurple,
                      size: 28,
                    ),
                    onPressed: () {
                      if (cancion != null) {
                        audioService.toggleFavorite();
                      }
                    },
                  ),

                  
                  // 📻 Boton de Radio
                  IconButton(
                    icon: const Icon(
                      Icons.radio,
                      color: Colors.deepPurple,
                      size: 30,
                    ),
                    onPressed: () {
                      final cancion = audioService.currentSong.value;
                      if (cancion != null) {
                        audioService.iniciarRadio(cancion);
                      }
                    },
                  ),


                  // ▶️ / ⏸️
                  ValueListenableBuilder<bool>(
                    valueListenable: audioService.isPlaying,
                    builder: (context, playing, _) {
                      return IconButton(
                        icon: Icon(
                          playing
                              ? Icons.pause_circle_filled
                              : Icons.play_circle_fill,
                          color: Colors.deepPurple,
                          size: 50,
                        ),
                        onPressed: () {
                          if (audioService.currentSong.value != null) {
                            audioService.togglePlayPause();
                          }
                        },
                      );
                    },
                  ),

                  // ⏭️ Siguiente
                  IconButton(
                    icon: const Icon(Icons.skip_next,
                        color: Colors.deepPurple, size: 32),
                    onPressed: () {
                      audioService.siguiente();
                    },
                  ),
                ],
              );
            },
          ),
        ],
      ),
    );
  }

  static String _formatDuration(Duration d) {
    final minutes = d.inMinutes.remainder(60).toString().padLeft(2, '0');
    final seconds = d.inSeconds.remainder(60).toString().padLeft(2, '0');
    return "$minutes:$seconds";
  }
}
