import 'package:flutter/material.dart';
import 'package:syncup_music_app/components/button_registro.dart';
import 'package:syncup_music_app/components/button_login.dart'; 

class Screen1Home extends StatefulWidget {
  const Screen1Home({super.key});

  @override
  State<Screen1Home> createState() => _Screen1HomeState();
}

class _Screen1HomeState extends State<Screen1Home> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white, 
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center, // Centra verticalmente
          children: [
            const Text(
              'Streaming de Música',
              style: TextStyle(
                fontSize: 22,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 24),

            // Logo de la app (debe estar en assets e incluido en pubspec.yaml)
            Image.asset(
              'assets/images/logo.png', // ruta del logo
              height: 150,
            ),
            const SizedBox(height: 40),

            // Botón Registro
            const ButtonRegistro(),
            const SizedBox(height: 20),

            // Botón Login
            const ButtonLogin(),
          ],
        ),
      ),
    );
  }
}
