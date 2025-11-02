import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:syncup_music_app/constants/constants.dart';
import 'package:uuid/uuid.dart'; // Asegúrate de agregar uuid en pubspec.yaml

class ScreenAdminAgregarCancion extends StatefulWidget {
  const ScreenAdminAgregarCancion({super.key});

  @override
  State<ScreenAdminAgregarCancion> createState() =>
      _ScreenAdminAgregarCancionState();
}

class _ScreenAdminAgregarCancionState extends State<ScreenAdminAgregarCancion> {
  final _formKey = GlobalKey<FormState>();

  final tituloCtrl = TextEditingController();
  final artistaCtrl = TextEditingController();
  final generoCtrl = TextEditingController();
  final anioCtrl = TextEditingController();
  final archivoCtrl = TextEditingController(text: 'default.mp3');

  Future<void> _agregarCancion() async {
    if (!_formKey.currentState!.validate()) return;

    final url = Uri.parse('$baseUrl/api/canciones');
    final body = json.encode({
      "id": const Uuid().v4(),
      "titulo": tituloCtrl.text.trim(),
      "artista": artistaCtrl.text.trim(),
      "genero": generoCtrl.text.trim(),
      "anio": anioCtrl.text.trim(),
      "nombreArchivo": archivoCtrl.text.trim(),
    });

    final response = await http.post(
      url,
      headers: {'Content-Type': 'application/json'},
      body: body,
    );

    if (response.statusCode == 200) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Canción agregada correctamente')),
      );
      Navigator.pop(context);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error al agregar: ${response.body}')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Agregar nueva canción',
            style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              TextFormField(
                controller: tituloCtrl,
                decoration: const InputDecoration(labelText: 'Título'),
                validator: (v) =>
                    v == null || v.isEmpty ? 'Ingresa el título' : null,
              ),
              TextFormField(
                controller: artistaCtrl,
                decoration: const InputDecoration(labelText: 'Artista'),
                validator: (v) =>
                    v == null || v.isEmpty ? 'Ingresa el artista' : null,
              ),
              TextFormField(
                controller: generoCtrl,
                decoration: const InputDecoration(labelText: 'Género'),
              ),
              TextFormField(
                controller: anioCtrl,
                decoration: const InputDecoration(labelText: 'Año'),
                keyboardType: TextInputType.number,
              ),
              TextFormField(
                controller: archivoCtrl,
                readOnly: true,
                decoration:
                    const InputDecoration(labelText: 'Nombre del archivo'),
              ),
              const SizedBox(height: 30),
              ElevatedButton(
                onPressed: _agregarCancion,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.deepPurple.shade200,
                  foregroundColor: Colors.black,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                ),
                child: const Text('Añadir canción'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
