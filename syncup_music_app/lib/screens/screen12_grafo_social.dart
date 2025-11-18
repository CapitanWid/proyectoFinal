import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:syncup_music_app/constants/constants.dart';

class ScreenGrafoSocial extends StatefulWidget {
  const ScreenGrafoSocial({super.key});

  @override
  State<ScreenGrafoSocial> createState() => _ScreenGrafoSocialState();
}

class _ScreenGrafoSocialState extends State<ScreenGrafoSocial> {
  List<dynamic> _usuarios = [];
  Set<String> _siguiendo = {}; 
  bool _cargando = true;

  final String usuarioActual = userUsuario;

  @override
  void initState() {
    super.initState();
    _inicializar();
  }

  // ======================================================
  // 🔹 Cargar usuarios + seguidos (estado persistente)
  // ======================================================
  Future<void> _inicializar() async {
    await _cargarUsuarios();
    await _cargarSiguiendo();
    setState(() => _cargando = false);
  }

  // ======================================================
  // 🔹 Cargar todos los usuarios del grafo
  // ======================================================
  Future<void> _cargarUsuarios() async {
    try {
      final url = Uri.parse('$baseUrl/api/grafo/usuarios');
      final res = await http.get(url);

      if (res.statusCode == 200) {
        _usuarios = jsonDecode(res.body);
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error cargando usuarios: $e"))
      );
    }
  }

  // ======================================================
  // 🔹 Cargar usuarios que YA sigo (desde backend)
  // ======================================================
  Future<void> _cargarSiguiendo() async {
    try {
      final url = Uri.parse('$baseUrl/api/grafo/siguiendo?usuario=$usuarioActual');
      final res = await http.get(url);

      if (res.statusCode == 200) {
        final data = jsonDecode(res.body) as List<dynamic>;

        _siguiendo = data
            .map((u) => u['usuario'].toString())
            .toSet();
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error cargando seguidos: $e"))
      );
    }
  }

  // ======================================================
  // 🔹 Seguir usuario
  // ======================================================
  Future<void> _seguirUsuario(String otro) async {
    try {
      final url = Uri.parse('$baseUrl/api/grafo/seguir?usuario1=$usuarioActual&usuario2=$otro');
      final res = await http.post(url);

      if (res.statusCode == 200) {
        setState(() => _siguiendo.add(otro));
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error al seguir a $otro: $e"))
      );
    }
  }

  // ======================================================
  // 🔹 Dejar de seguir usuario
  // ======================================================
  Future<void> _dejarDeSeguir(String otro) async {
    try {
      final url = Uri.parse('$baseUrl/api/grafo/dejarSeguir?usuario1=$usuarioActual&usuario2=$otro');
      final res = await http.post(url);

      if (res.statusCode == 200) {
        setState(() => _siguiendo.remove(otro));
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error al dejar de seguir a $otro: $e"))
      );
    }
  }

  // ======================================================
  // 🔹 INTERFAZ
  // ======================================================
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Grafo Social"),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => Navigator.pop(context),
        ),
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
                    final nombreUsuario = usuario['usuario'] ?? '';
                    final nombreCompleto =
                        "${usuario['nombre'] ?? ''} ${usuario['apellido'] ?? ''}";

                    // No mostrar al usuario actual
                    if (nombreUsuario == usuarioActual) {
                      return const SizedBox.shrink();
                    }

                    final bool siguiendo = _siguiendo.contains(nombreUsuario);

                    return Card(
                      margin: const EdgeInsets.symmetric(vertical: 6),
                      color: const Color(0xFFF8F5FF),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: ListTile(
                        title: Text(
                          nombreUsuario,
                          style: const TextStyle(fontWeight: FontWeight.w600),
                        ),
                        subtitle: Text(
                          nombreCompleto,
                          style: const TextStyle(fontSize: 14),
                        ),
                        trailing: ElevatedButton(
                          onPressed: () {
                            if (siguiendo) {
                              _dejarDeSeguir(nombreUsuario);
                            } else {
                              _seguirUsuario(nombreUsuario);
                            }
                          },
                          style: ElevatedButton.styleFrom(
                            backgroundColor:
                                siguiendo ? Colors.red.shade100 : Colors.grey.shade300,
                            foregroundColor: Colors.black,
                            elevation: 2,
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(18),
                            ),
                          ),
                          child: Text(siguiendo ? "Siguiendo" : "Seguir"),
                        ),
                      ),
                    );
                  },
                ),
    );
  }
}
