import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';

class ScreenRegistroForm extends StatefulWidget {
  const ScreenRegistroForm({super.key});

  @override
  State<ScreenRegistroForm> createState() => _ScreenRegistroFormState();
}

class _ScreenRegistroFormState extends State<ScreenRegistroForm> {
  final _formKey = GlobalKey<FormState>();

  // Controladores de texto
  final nombreController = TextEditingController();
  final apellidoController = TextEditingController();
  final correoController = TextEditingController();
  final usuarioController = TextEditingController();
  final passwordController = TextEditingController();
  final repeatPasswordController = TextEditingController();

  Future<void> _guardarDatos() async {
    final data = {
      'nombre': nombreController.text.trim(),
      'apellido': apellidoController.text.trim(),
      'correo': correoController.text.trim(),
      'usuario': usuarioController.text.trim(),
      'contrasena': passwordController.text.trim(),
    };

    // Verificar y solicitar permiso de almacenamiento para Android 11+
    final permissionStatus = await Permission.manageExternalStorage.request();

    if (!permissionStatus.isGranted) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text("❌ Permiso de almacenamiento denegado. Ábrelo manualmente."),
          ),
        );
      }

      // Abrir la configuración de la app para que el usuario lo active
      await openAppSettings();
      return;
    }

    try {
      // Ruta a la carpeta Descargas
      final downloadsDir = Directory('/storage/emulated/0/Download');
      final file = File('${downloadsDir.path}/registro_data.txt');

      await file.writeAsString(jsonEncode(data));

      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("✅ Registro guardado en Descargas")),
        );
      }

      print("Archivo guardado en: ${file.path}");
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("❌ Error al guardar el archivo: $e")),
        );
      }
    }
  }

  void _registrar() {
    if (_formKey.currentState!.validate()) {
      _guardarDatos();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Registro por correo"),
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              TextFormField(
                controller: nombreController,
                decoration: const InputDecoration(labelText: 'Nombre'),
                validator: (value) =>
                    value == null || value.isEmpty ? 'Campo requerido' : null,
              ),
              TextFormField(
                controller: apellidoController,
                decoration: const InputDecoration(labelText: 'Apellido'),
                validator: (value) =>
                    value == null || value.isEmpty ? 'Campo requerido' : null,
              ),
              TextFormField(
                controller: correoController,
                decoration:
                    const InputDecoration(labelText: 'Correo electrónico'),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Campo requerido';
                  } else if (!value.contains('@') || !value.contains('.com')) {
                    return 'Correo inválido';
                  }
                  return null;
                },
              ),
              TextFormField(
                controller: usuarioController,
                decoration: const InputDecoration(labelText: 'Usuario'),
                validator: (value) =>
                    value == null || value.isEmpty ? 'Campo requerido' : null,
              ),
              TextFormField(
                controller: passwordController,
                obscureText: true,
                decoration: const InputDecoration(labelText: 'Contraseña'),
                validator: (value) =>
                    value == null || value.isEmpty ? 'Campo requerido' : null,
              ),
              TextFormField(
                controller: repeatPasswordController,
                obscureText: true,
                decoration:
                    const InputDecoration(labelText: 'Repetir contraseña'),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Campo requerido';
                  } else if (value != passwordController.text) {
                    return 'Las contraseñas no coinciden';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: _registrar,
                child: const Text("Registro"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
