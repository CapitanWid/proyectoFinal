import 'package:flutter/material.dart';
import 'package:syncup_music_app/screens/screen2_registro.dart';

class ButtonRegistro extends StatelessWidget {
  const ButtonRegistro({super.key});

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: () {
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const ScreenRegistroTipo()),
        );
      },
      child: const Text('Registro'),
    );
  }
}
