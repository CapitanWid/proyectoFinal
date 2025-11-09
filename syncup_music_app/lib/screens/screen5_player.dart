import 'package:flutter/material.dart';
import 'package:syncup_music_app/screens/screen10_favoritos.dart';
import 'package:syncup_music_app/screens/screen11_descubrimiento_semanal.dart';
import 'package:syncup_music_app/screens/screen12_grafo_social.dart';
import 'package:syncup_music_app/screens/screen7_perfil_usuario.dart';
import 'package:syncup_music_app/screens/screen8_busqueda.dart';
import 'package:syncup_music_app/widgets/audio_player_widget.dart';
import 'package:syncup_music_app/service/audio_player_service.dart';

class Screen5Player extends StatefulWidget {
  const Screen5Player({super.key});

  @override
  State<Screen5Player> createState() => _Screen5PlayerState();
}

class _Screen5PlayerState extends State<Screen5Player> {
  final AudioPlayerService _audioService = AudioPlayerService();

  Widget _buildMenuButton(IconData icon, String label, VoidCallback onPressed) {
    return Container(
      width: double.infinity,
      margin: const EdgeInsets.symmetric(vertical: 8),
      child: ElevatedButton.icon(
        onPressed: onPressed,
        icon: Icon(icon),
        label: Text(label),
        style: ElevatedButton.styleFrom(
          padding: const EdgeInsets.symmetric(vertical: 18),
          backgroundColor: Colors.grey.shade200,
          foregroundColor: Colors.black,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
        ),
      ),
    );
  }

  Widget _buildScrollableButtons() {
    return ListView(
      children: [
        _buildMenuButton(Icons.search, "Buscar canciones", () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (_) => const BusquedaScreen()),
          );
        }),
        _buildMenuButton(Icons.favorite, "Favoritos", () {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (_) => FavoritosScreen(
              usuarioId: AudioPlayerService().usuarioId, // usa el id del usuario logueado
            ),
          ),
        );
      }),

        _buildMenuButton(Icons.explore, "Descubrimiento semanal", () {
          Navigator.push(
          context,
          MaterialPageRoute(
            builder: (_) => const ScreenDescubrimientoSemanal(),
          ),
        );


        }),

      
        _buildMenuButton(Icons.people_alt, "Conectar con usuarios", () {
          Navigator.push(
          context,
          MaterialPageRoute(
            builder: (_) => const ScreenGrafoSocial(),
          ),
        );
        }),
        //_buildMenuButton(Icons.group, "Conectar", () {}),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        titleSpacing: 0,
        title: Row(
          children: [
            const SizedBox(width: 12),
            const Text("Usuario", style: TextStyle(fontSize: 20)),
            const Spacer(),
            IconButton(
              icon: const CircleAvatar(
                backgroundColor: Colors.deepPurple,
                child: Icon(Icons.person, color: Colors.white),
              ),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (_) => const PerfilScreen()),
                );
              },
            ),
          ],
        ),
      ),

      // 🔹 Ahora el body se expande y usa todo el espacio disponible sin dejar hueco
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 20),
        child: _buildScrollableButtons(),
      ),

      // 🔹 Reproductor fijo al fondo
      bottomNavigationBar: Padding(
        padding: const EdgeInsets.fromLTRB(12, 0, 12, 12),
        child: const AudioPlayerWidget(),
      ),
    );
  }
} 
