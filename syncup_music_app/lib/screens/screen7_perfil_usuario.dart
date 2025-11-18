import 'dart:io';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:permission_handler/permission_handler.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'package:syncup_music_app/constants/constants.dart';
import 'package:syncup_music_app/screens/screen1_home.dart';
import 'package:syncup_music_app/screens/screen9_modificar_perfil_usuario.dart';
import 'package:syncup_music_app/service/audio_player_service.dart';

class PerfilScreen extends StatelessWidget {
  const PerfilScreen({super.key});

  // ===============================================================
  //  GUARDAR CSV EN CARPETA DE DESCARGAS REAL
  // ===============================================================
  Future<void> _exportarFavoritos(BuildContext context) async {
    try {
      // Permisos normales
      await Permission.storage.request();

      // Android 11, 12, 13 → requiere permiso extra
      if (await Permission.manageExternalStorage.isDenied) {
        await Permission.manageExternalStorage.request();
      }

      final prefs = await SharedPreferences.getInstance();
      final usuario = prefs.getString('usuario') ?? userUsuario;

      final url = Uri.parse('$baseUrl/api/favoritos/$usuario/csv');
      final response = await http.get(url);

      if (response.statusCode == 200) {
        // 📁 Carpeta REAL de Descargas
        final downloads = Directory('/storage/emulated/0/Download');

        if (!downloads.existsSync()) {
          downloads.createSync(recursive: true);
        }

        final filePath = '${downloads.path}/favoritos_syncup.csv';
        final file = File(filePath);

        await file.writeAsBytes(response.bodyBytes);

        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text("✅ CSV guardado en Descargas:\n$filePath"),
          ),
        );

      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text("❌ Error del servidor: ${response.statusCode}"),
          ),
        );
      }

    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("❌ Error exportando CSV: $e")),
      );
    }
  }

  // ===============================================================
  //  CERRAR SESIÓN
  // ===============================================================
  Future<void> _logout(BuildContext context) async {
    final audioService = AudioPlayerService();
    audioService.reset();
    //await audioService.player.stop();

    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('isLoggedIn', false);

    Navigator.of(context).pushAndRemoveUntil(
      MaterialPageRoute(builder: (_) => const Screen1Home()),
      (route) => false,
    );
  }

  // ===============================================================
  // 🔹 BOTÓN REUTILIZABLE
  // ===============================================================
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

  // ===============================================================
  // 🔹 UI
  // ===============================================================
 

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Perfil")),
      body: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            _buildMenuButton(
              Icons.edit,
              "Modificar perfil",
              () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (_) => const EditarPerfilScreen()),
                );
              },
            ),

            // =====================================================
            // 🔥 MOSTRAR SOLO SI NO ES ADMIN
            // =====================================================
            if (userTipo != "ADMIN")
              _buildMenuButton(
                Icons.file_download,
                "Exportar favoritos (.csv)",
                () => _exportarFavoritos(context),
              ),

            _buildMenuButton(
              Icons.logout,
              "Cerrar sesión",
              () => _logout(context),
            ),
          ],
        ),
      ),
    );
  }


}
