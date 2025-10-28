import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:syncup_music_app/screens/screen1_home.dart';

import 'package:syncup_music_app/constants/constants.dart';


// Importa la pantalla del reproductor
import 'package:syncup_music_app/screens/reproductor_screen.dart';

class ScreenRegistroForm extends StatefulWidget {
  const ScreenRegistroForm({super.key});

  @override
  State<ScreenRegistroForm> createState() => _ScreenRegistroFormState();
}

class _ScreenRegistroFormState extends State<ScreenRegistroForm> {
  final _formKey = GlobalKey<FormState>();

  final nombreController = TextEditingController();
  final apellidoController = TextEditingController();
  final correoController = TextEditingController();
  final usuarioController = TextEditingController();
  final passwordController = TextEditingController();
  final repeatPasswordController = TextEditingController();

  Future<void> _enviarDatosAlServidor() async {
    //final url = Uri.parse('http://10.0.2.2:8080/api/formulario');
    //final url = Uri.parse('http://192.168.5.2:8080/api/formulario');
    final url = Uri.parse('$baseUrl/api/formulario');

    

    final data = {
      'nombre': nombreController.text.trim(),
      'apellido': apellidoController.text.trim(),
      'correo': correoController.text.trim(),
      'usuario': usuarioController.text.trim(),
      'contrasena': passwordController.text.trim(),
      'repetirContrasena': repeatPasswordController.text.trim(),
    };

    try {
      final response = await http.post(
        url,
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(data),
      );

      if (response.statusCode == 200) {
        final responseBody = response.body.toLowerCase();

        if (responseBody.contains('usuario ya existe')) {
          if (context.mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text("❌ El usuario ya existe, elige otro.")),
            );
          }
        } else if (responseBody.contains('formulario recibido')) {
          if (context.mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text("✅ Registro exitoso!")),
            );

            // Esperar 1 segundo y regresar a ScreenRegistroTipo
            Future.delayed(const Duration(seconds: 1), () {
            if (context.mounted) {
              Navigator.pushReplacement(
                context,
                MaterialPageRoute(builder: (context) => const Screen1Home()),
              );
            }
          });

          }
        } else {
          if (context.mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text("❌ Respuesta inesperada del servidor: ${response.body}")),
            );
          }
        }
      } else {
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text("❌ Error del servidor: ${response.statusCode}")),
          );
        }
      }
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("❌ Error de conexión: $e")),
        );
      }
    }
  }

  void _registrar() {
    if (_formKey.currentState!.validate()) {
      _enviarDatosAlServidor();
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
                decoration: const InputDecoration(labelText: 'Correo electrónico'),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Campo requerido';
                  } else if (!value.contains('@') || !value.contains('.')) {
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
                decoration: const InputDecoration(labelText: 'Repetir contraseña'),
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
                child: const Text("Registrarse"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
