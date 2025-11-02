import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:syncup_music_app/constants/constants.dart'; // baseUrl definido aquí

class ScreenGestionarUsuarios extends StatefulWidget {
  const ScreenGestionarUsuarios({super.key});

  @override
  State<ScreenGestionarUsuarios> createState() => _ScreenGestionarUsuariosState();
}

class _ScreenGestionarUsuariosState extends State<ScreenGestionarUsuarios> {
  List<dynamic> _usuarios = [];
  bool _cargando = true;

  @override
  void initState() {
    super.initState();
    _cargarUsuarios();
  }

  // ======================================================
  // OBTENER TODOS LOS USUARIOS
  // ======================================================
  Future<void> _cargarUsuarios() async {
    try {
      final url = Uri.parse('$baseUrl/api/usuarios');
      final response = await http.get(url);

      if (response.statusCode == 200) {
        setState(() {
          _usuarios = json.decode(response.body);
          _cargando = false;
        });
      } else {
        throw Exception("Error al obtener usuarios: ${response.statusCode}");
      }
    } catch (e) {
      setState(() => _cargando = false);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error de conexión: $e")),
      );
    }
  }

  // ======================================================
  // ELIMINAR USUARIO (CONFIRMADO)
  // ======================================================
  Future<void> _eliminarUsuario(String usuario) async {
    try {
      final url = Uri.parse('$baseUrl/api/usuarios/$usuario');
      final response = await http.delete(url);

      if (response.statusCode == 200) {
        setState(() {
          _usuarios.removeWhere((u) => u['usuario'] == usuario);
        });
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Usuario eliminado exitosamente")),
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("Error al eliminar usuario (${response.statusCode})")),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error de conexión: $e")),
      );
    }
  }

  // ======================================================
  // MOSTRAR DETALLES DE UN USUARIO
  // ======================================================
  void _mostrarDetallesUsuario(Map<String, dynamic> usuario) {
    showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: Text("Usuario: ${usuario['usuario']}"),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text("Nombre: ${usuario['nombre']} ${usuario['apellido']}"),
            Text("Correo: ${usuario['correo']}"),
            const SizedBox(height: 20),
            Center(
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.red.shade700,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 24),
                ),
                onPressed: () {
                  Navigator.pop(context); // Cierra el primer diálogo
                  _confirmarEliminacion(usuario['usuario']);
                },
                child: const Text("Eliminar usuario"),
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text("Cerrar"),
          ),
        ],
      ),
    );
  }

  // ======================================================
  // CONFIRMAR ELIMINACIÓN
  // ======================================================
  void _confirmarEliminacion(String usuario) {
    showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text("Confirmar eliminación"),
        content: const Text("¿Seguro que deseas eliminar este usuario?"),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text("Cancelar"),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              _eliminarUsuario(usuario);
            },
            child: const Text("Confirmar", style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
  }

  // ======================================================
  // INTERFAZ
  // ======================================================
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Gestionar Usuarios"),
      ),
      body: _cargando
          ? const Center(child: CircularProgressIndicator())
          : _usuarios.isEmpty
              ? const Center(child: Text("No hay usuarios registrados"))
              : ListView.builder(
                  padding: const EdgeInsets.all(12),
                  itemCount: _usuarios.length,
                  itemBuilder: (context, index) {
                    final usuario = _usuarios[index];
                    return Card(
                      margin: const EdgeInsets.symmetric(vertical: 6),
                      child: ListTile(
                        title: Text(usuario['usuario'] ?? 'Sin usuario'),
                        subtitle: Text(
                          "${usuario['nombre'] ?? ''} ${usuario['apellido'] ?? ''}",
                          style: const TextStyle(fontSize: 14),
                        ),
                        trailing: ElevatedButton(
                          onPressed: () => _mostrarDetallesUsuario(usuario),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Colors.grey.shade300,
                            foregroundColor: Colors.black,
                          ),
                          child: const Text("Ver"),
                        ),
                      ),
                    );
                  },
                ),
    );
  }
}
