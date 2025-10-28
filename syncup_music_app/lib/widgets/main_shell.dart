import 'package:flutter/material.dart';
import 'package:syncup_music_app/screens/screen5_player.dart';
import 'package:syncup_music_app/screens/screen8_busqueda.dart';
import 'package:syncup_music_app/widgets/audio_player_widget.dart';
import 'package:syncup_music_app/service/audio_player_service.dart';

class MainShell extends StatefulWidget {
  const MainShell({super.key});

  @override
  State<MainShell> createState() => _MainShellState();
}

class _MainShellState extends State<MainShell> {
  int _selectedIndex = 0;

  final List<Widget> _screens = const [
    Screen5Player(),
    BusquedaScreen(),
  ];

  final AudioPlayerService _audioService = AudioPlayerService(); // ✅ Singleton global

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          // Contenido de la pestaña seleccionada
          _screens[_selectedIndex],

          // 🔹 Reproductor siempre visible (incluso sin canción)
          Align(
            alignment: Alignment.bottomCenter,
            child: Padding(
              padding: const EdgeInsets.all(8.0),
              child: Material(
                elevation: 10,
                borderRadius: const BorderRadius.all(Radius.circular(16)),
                child: const AudioPlayerWidget(),
              ),
            ),
          ),
        ],
      ),

      // 🔹 Barra inferior de navegación
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        onTap: (index) => setState(() => _selectedIndex = index),
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home), label: 'Inicio'),
          BottomNavigationBarItem(icon: Icon(Icons.search), label: 'Buscar'),
        ],
      ),
    );
  }
}
