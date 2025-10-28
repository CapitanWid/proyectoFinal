import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:syncup_music_app/screens/screen1_home.dart';
import 'package:syncup_music_app/screens/screen9_modificar_perfil_usuario.dart';
import 'package:syncup_music_app/service/audio_player_service.dart';

class PerfilScreen extends StatelessWidget {
  const PerfilScreen({super.key});

  Future<void> _logout(BuildContext context) async {

    final audioService = AudioPlayerService();
    await audioService.player.stop();

    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('isLoggedIn', false);

    // Redirigir y reemplazar la ruta actual para que no pueda volver atrás
    Navigator.of(context).pushAndRemoveUntil(
      MaterialPageRoute(builder: (context) => const Screen1Home()),
      (Route<dynamic> route) => false,
    );
  }

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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Perfil"),
      ),
      body: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            _buildMenuButton(Icons.edit, "Modificar perfil", () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const EditarPerfilScreen()),
              );
            }),

            _buildMenuButton(Icons.logout, "Cerrar sesión", () => _logout(context)),
          ],
        ),
      ),
    );
  }
}
