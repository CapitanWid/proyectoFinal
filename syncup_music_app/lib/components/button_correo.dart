import 'package:flutter/material.dart';
import 'package:syncup_music_app/screens/screen3_registro_form.dart';


class ButtonCorreo extends StatelessWidget {
  const ButtonCorreo({super.key});

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: () {
        Navigator.push(
          context,
          MaterialPageRoute(builder: (_) => const ScreenRegistroForm()),
        );
      },
      child: const Text('Usar correo electrónico'),
    );
  }
}
