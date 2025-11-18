import 'package:flutter/material.dart';
import 'package:just_audio/just_audio.dart';
import 'package:http/http.dart' as http;
import 'package:syncup_music_app/constants/constants.dart';

class ReproductorScreen extends StatefulWidget {
  const ReproductorScreen({super.key});

  @override
  State<ReproductorScreen> createState() => _ReproductorScreenState();
}

class _ReproductorScreenState extends State<ReproductorScreen> {
  final AudioPlayer _player = AudioPlayer();
  bool _cargando = false;

 final url = '$baseUrl/api/canciones/minero.mp3';
  

  Future<void> reproducirCancion() async {
    setState(() {
      _cargando = true;
    });

    try {
      // 🔍 Primero verifica si la URL es accesible
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        await _player.setUrl(url);
        await _player.play();
      } else {
        throw Exception('No se pudo acceder al archivo. Código HTTP: ${response.statusCode}');
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('❌ Error: $e')),
      );
    } finally {
      setState(() {
        _cargando = false;
      });
    }
  }

  @override
  void dispose() {
    _player.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("🎵 Reproductor")),
      body: Center(
        child: _cargando
            ? const CircularProgressIndicator()
            : ElevatedButton.icon(
                onPressed: reproducirCancion,
                icon: const Icon(Icons.play_arrow),
                label: const Text("Reproducir canción"),
              ),
      ),
    );
  }
}
