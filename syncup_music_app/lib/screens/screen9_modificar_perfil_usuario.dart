import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:syncup_music_app/constants/constants.dart';

class EditarPerfilScreen extends StatefulWidget {
  const EditarPerfilScreen({super.key});

  @override
  State<EditarPerfilScreen> createState() => _EditarPerfilScreenState();
}

class _EditarPerfilScreenState extends State<EditarPerfilScreen> {
  final _formKey = GlobalKey<FormState>();

  final nombreController = TextEditingController();
  final apellidoController = TextEditingController();
  final correoController = TextEditingController();
  final actualPasswordController = TextEditingController();
  final nuevaPasswordController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _cargarDatosUsuario();
  }

  Future<void> _cargarDatosUsuario() async {
    final prefs = await SharedPreferences.getInstance();

    nombreController.text = userNombre.isNotEmpty
        ? userNombre
        : (prefs.getString('nombre') ?? '');
    apellidoController.text = userApellido.isNotEmpty
        ? userApellido
        : (prefs.getString('apellido') ?? '');
    correoController.text = userCorreo.isNotEmpty
        ? userCorreo
        : (prefs.getString('correo') ?? '');
  }

  Future<void> _actualizarDatos() async {
    if (!_formKey.currentState!.validate()) return;

    if (actualPasswordController.text.trim() != userPassword.trim()) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("❌ La contraseña actual no coincide")),
      );
      return;
    }

    final nuevaPassword = nuevaPasswordController.text.trim().isNotEmpty
        ? nuevaPasswordController.text.trim()
        : userPassword;

    final prefs = await SharedPreferences.getInstance();
    final usuario = prefs.getString('usuario') ?? userUsuario;

    final url = Uri.parse('$baseUrl/api/formulario/actualizar/$usuario');
    final data = {
      'nombre': nombreController.text.trim(),
      'apellido': apellidoController.text.trim(),
      'correo': correoController.text.trim(),
      'contrasena': nuevaPassword,
    };

    try {
      final response = await http.put(
        url,
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(data),
      );

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("✅ Perfil actualizado correctamente")),
        );

        userNombre = data['nombre']!;
        userApellido = data['apellido']!;
        userCorreo = data['correo']!;
        userPassword = data['contrasena']!;

        await prefs.setString('nombre', userNombre);
        await prefs.setString('apellido', userApellido);
        await prefs.setString('correo', userCorreo);
        await prefs.setString('contrasena', userPassword);
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("❌ Error del servidor: ${response.statusCode}")),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("❌ Error de conexión: $e")),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Editar perfil")),
      body: Padding(
        padding: const EdgeInsets.all(20),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              Text(
                "Usuario: $userUsuario",
                style: const TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const Divider(height: 30),

              TextFormField(
                controller: nombreController,
                decoration: const InputDecoration(labelText: 'Nombre'),
                validator: (v) =>
                    v == null || v.isEmpty ? 'Campo requerido' : null,
              ),
              TextFormField(
                controller: apellidoController,
                decoration: const InputDecoration(labelText: 'Apellido'),
                validator: (v) =>
                    v == null || v.isEmpty ? 'Campo requerido' : null,
              ),
              TextFormField(
                controller: correoController,
                decoration:
                    const InputDecoration(labelText: 'Correo electrónico'),
                validator: (v) =>
                    v == null || v.isEmpty ? 'Campo requerido' : null,
              ),
              const SizedBox(height: 20),

              TextFormField(
                controller: actualPasswordController,
                obscureText: true,
                decoration:
                    const InputDecoration(labelText: 'Contraseña actual'),
                validator: (v) =>
                    v == null || v.isEmpty ? 'Campo requerido' : null,
              ),
              TextFormField(
                controller: nuevaPasswordController,
                obscureText: true,
                decoration: const InputDecoration(
                    labelText: 'Nueva contraseña (opcional)'),
              ),
              const SizedBox(height: 30),

              ElevatedButton(
                onPressed: _actualizarDatos,
                style: ElevatedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(10),
                  ),
                ),
                child: const Text("Guardar cambios"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
