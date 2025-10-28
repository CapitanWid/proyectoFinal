import 'package:flutter/material.dart';
import 'package:syncup_music_app/screens/screen4_login.dart'; // importa tu pantalla de login

class ButtonLogin extends StatefulWidget {
  const ButtonLogin({super.key});

  @override
  State<ButtonLogin> createState() => _ButtonLoginState();
}

class _ButtonLoginState extends State<ButtonLogin> {
  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: () {
        // Navegar a la pantalla de login usando MaterialPageRoute
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const ScreenLogin()),
        );
      },
      child: const Text('Login'),
    );
  }
}
