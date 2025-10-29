import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:syncup_music_app/constants/constants.dart';
import 'package:syncup_music_app/screens/screen5_player.dart';
import 'package:syncup_music_app/screens_admin/screen1_admin_principal.dart';
import 'package:syncup_music_app/service/audio_player_service.dart';

class ScreenLogin extends StatefulWidget {
  const ScreenLogin({super.key});

  @override
  State<ScreenLogin> createState() => _ScreenLoginState();
}

class _ScreenLoginState extends State<ScreenLogin> {
  final _formKey = GlobalKey<FormState>();
  final usuarioController = TextEditingController();
  final passwordController = TextEditingController();
  bool _cargando = false;

  // ==========================================
  // FUNCIÓN PRINCIPAL: INICIAR SESIÓN
  // ==========================================
  Future<void> _iniciarSesion() async {
    setState(() {
      _cargando = true;
    });

    final url = Uri.parse('$baseUrl/api/login');

    final data = {
      'usuario': usuarioController.text.trim(),
      'contrasena': passwordController.text.trim(),
    };

    try {
      final response = await http.post(
        url,
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(data),
      );

      if (response.statusCode == 200) {
        final userData = jsonDecode(response.body);

        // Guardar datos globalmente en constants.dart
        userUsuario = userData['usuario'];
        userNombre = userData['nombre'];
        userApellido = userData['apellido'];
        userCorreo = userData['correo'];
        userPassword = userData['contrasena']; // ← agregado
        userTipo = userData['tipo'];


        final audioService = AudioPlayerService();
        audioService.usuarioId = userUsuario;

        // Guardar también en SharedPreferences
        final prefs = await SharedPreferences.getInstance();
        await prefs.setBool('isLoggedIn', true);
        await prefs.setString('usuario', userUsuario);
        await prefs.setString('nombre', userNombre);
        await prefs.setString('apellido', userApellido);
        await prefs.setString('correo', userCorreo);
        await prefs.setString('contrasena', userPassword); // ← agregado
        await prefs.setString('tipo', userTipo);


        debugPrint("🟣 Datos recibidos del servidor: $userData");
        debugPrint("🟣 Tipo de usuario: ${userData['tipo']}");



        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('✅ Inicio de sesión exitoso')),
          );

          

          if (userTipo == 'ADMIN') {
            Navigator.pushReplacement(
              context,
              MaterialPageRoute(builder: (context) => const ScreenAdmin()),
            );
          } else {
            Navigator.pushReplacement(
              context,
              MaterialPageRoute(builder: (context) => const Screen5Player()),
            );


        }
        }
      } else if (response.statusCode == 401) {
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('❌ Usuario o contraseña incorrectos')),
          );
        }
      } else {
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('❌ Error del servidor: ${response.statusCode}')),
          );
        }
      }
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('❌ Error de conexión: $e')),
        );
      }
    } finally {
      setState(() {
        _cargando = false;
      });
    }
  }

  // ==========================================
  // VALIDACIÓN Y ENVÍO
  // ==========================================
  void _validarYEnviar() {
    if (_formKey.currentState!.validate()) {
      _iniciarSesion();
    }
  }

  // ==========================================
  // INTERFAZ GRÁFICA
  // ==========================================
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Iniciar sesión')),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
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
              const SizedBox(height: 20),
              _cargando
                  ? const CircularProgressIndicator()
                  : ElevatedButton(
                      onPressed: _validarYEnviar,
                      child: const Text('Iniciar sesión'),
                    ),
            ],
          ),
        ),
      ),
    );
  }
}
