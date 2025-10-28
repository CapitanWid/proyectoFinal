import 'package:flutter/material.dart';
import 'package:syncup_music_app/components/button_correo.dart';
import 'package:syncup_music_app/components/button_usuario.dart';

class ScreenRegistroTipo extends StatelessWidget {
  const ScreenRegistroTipo({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Registro'),
        centerTitle: true,
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: const [
            ButtonCorreo(),
            SizedBox(height: 20),
            ButtonUsuario(),
          ],
        ),
      ),
    );
  }
}
