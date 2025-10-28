import 'package:flutter/material.dart';

class ButtonUsuario extends StatelessWidget {
  const ButtonUsuario({super.key});

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: () {
        // Acción futura para usar nombre de usuario
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Usar nombre de usuario')),
        );
      },
      child: const Text('Usuario'),
    );
  }
}
