import 'package:flutter/material.dart';
import 'package:syncup_music_app/widgets/audio_player_widget.dart';
import 'package:syncup_music_app/service/audio_player_service.dart';
import 'package:syncup_music_app/screens/screen7_perfil_usuario.dart';

// ======================================================
// PANTALLA PRINCIPAL DEL ADMINISTRADOR
// ======================================================
class ScreenAdmin extends StatefulWidget {
  const ScreenAdmin({super.key});

  @override
  State<ScreenAdmin> createState() => _ScreenAdminState();
}

class _ScreenAdminState extends State<ScreenAdmin> {
  final AudioPlayerService _audioService = AudioPlayerService();

  // ======================================================
  // BOTÓN GENERAL DE MENÚ
  // ======================================================
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

  // ======================================================
  // MENÚ SCROLLABLE DEL ADMINISTRADOR
  // ======================================================
  Widget _buildScrollableButtons() {
    return ListView(
      children: [
        _buildMenuButton(Icons.supervised_user_circle, "Gestionar usuarios", () {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text("Abrir pantalla de gestión de usuarios")),
          );
        }),
        _buildMenuButton(Icons.library_music, "Gestionar canciones", () {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text("Abrir pantalla de gestión de canciones")),
          );
        }),
        _buildMenuButton(Icons.playlist_add, "Subir nueva canción", () {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text("Abrir pantalla para subir canciones")),
          );
        }),
        _buildMenuButton(Icons.analytics, "Ver estadísticas", () {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text("Abrir pantalla de estadísticas")),
          );
        }),
        _buildMenuButton(Icons.report, "Reportes de usuarios", () {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text("Abrir pantalla de reportes de usuarios")),
          );
        }),
        _buildMenuButton(Icons.settings, "Configuración del sistema", () {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text("Abrir pantalla de configuración")),
          );
        }),
      ],
    );
  }

  // ======================================================
  // CONSTRUCCIÓN DE LA INTERFAZ
  // ======================================================
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        titleSpacing: 0,
        title: Row(
          children: [
            const SizedBox(width: 12),
            const Text("Administrador", style: TextStyle(fontSize: 20)),
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

      // 🔹 Menú principal del administrador
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
