import 'package:flutter/material.dart';
import 'package:syncup_music_app/screens/screen3_registro_form.dart';


class ButtonRegistro extends StatelessWidget {
  const ButtonRegistro({super.key});

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: () {
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const ScreenRegistroForm()),
        );
      },
      child: const Text('Registro'),
    );
  }
}
