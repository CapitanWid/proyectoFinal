import 'package:flutter/material.dart';
import 'package:syncup_music_app/screens/screen7_perfil_usuario.dart';
import 'package:syncup_music_app/screens_admin/screen2_gestionar_usuarios.dart';
import 'package:syncup_music_app/screens_admin/screen3_admin_gestionar_canciones.dart';
import 'package:syncup_music_app/screens_admin/screen6_admin_metricas.dart';
import 'package:syncup_music_app/screens_admin/screen7_admin_carga_masiva.dart';

// ======================================================
// PANTALLA PRINCIPAL DEL ADMINISTRADOR
// ======================================================
class ScreenAdmin extends StatefulWidget {
  const ScreenAdmin({super.key});

  @override
  State<ScreenAdmin> createState() => _ScreenAdminState();
}

class _ScreenAdminState extends State<ScreenAdmin> {
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
          Navigator.push(
            context,
            MaterialPageRoute(builder: (_) => const ScreenGestionarUsuarios()),
          );
        }),

        _buildMenuButton(Icons.library_music, "Gestionar canciones", () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (_) => const ScreenAdminGestionarCanciones()),
          );
        }),
        _buildMenuButton(Icons.playlist_add, "Subir nueva canción", () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (_) => const ScreenAdminCargarCanciones()),
          );
        }),
        _buildMenuButton(Icons.analytics, "Ver estadísticas", () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (_) => const ScreenAdminMetricas()),
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
        automaticallyImplyLeading: false,
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
    );
  }
}
