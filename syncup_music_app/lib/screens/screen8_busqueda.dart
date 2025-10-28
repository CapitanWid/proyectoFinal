import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:syncup_music_app/constants/constants.dart';
import 'package:syncup_music_app/model/cancion.dart';
import 'package:syncup_music_app/service/audio_player_service.dart';
import 'package:syncup_music_app/widgets/audio_player_widget.dart';

class BusquedaScreen extends StatefulWidget {
  const BusquedaScreen({super.key});

  @override
  State<BusquedaScreen> createState() => _BusquedaScreenState();
}

class _BusquedaScreenState extends State<BusquedaScreen> {
  final AudioPlayerService _audioService = AudioPlayerService();

  final TextEditingController _tituloController = TextEditingController();
  final TextEditingController _artistaController = TextEditingController();
  final TextEditingController _generoController = TextEditingController();
  final TextEditingController _anioController = TextEditingController();

  bool _busquedaAvanzada = false;
  bool _isLoading = false;

  String _logicaArtista = "NINGUNO";
  String _logicaGenero = "NINGUNO";
  String _logicaAnio = "NINGUNO";

  List<String> _sugerencias = [];
  List<Cancion> _resultados = [];

  Future<void> _buscarSugerencias(String query) async {
    if (query.length < 2) return;

    try {
      final response = await http.get(
        Uri.parse('$baseUrl/api/canciones/autocompletar?query=$query'),
      );
      if (response.statusCode == 200) {
        final List<dynamic> jsonList = jsonDecode(response.body);
        setState(() {
          _sugerencias = List<String>.from(jsonList);
        });
      }
    } catch (e) {
      debugPrint("Error en autocompletado: $e");
    }
  }


  Future<void> _buscarCanciones() async {
    setState(() => _isLoading = true);

    final filtros = <Map<String, dynamic>>[];

    // --- Solo agregamos filtros válidos ---
    if (_busquedaAvanzada) {
      if (_logicaArtista != "NINGUNO" &&
          _artistaController.text.trim().isNotEmpty) {
        filtros.add({
          "logica": _logicaArtista,
          "campo": "artista",
          "valor": _artistaController.text.trim()
        });
      }

      if (_logicaGenero != "NINGUNO" &&
          _generoController.text.trim().isNotEmpty) {
        filtros.add({
          "logica": _logicaGenero,
          "campo": "genero",
          "valor": _generoController.text.trim()
        });
      }

      if (_logicaAnio != "NINGUNO" &&
          _anioController.text.trim().isNotEmpty) {
        filtros.add({
          "logica": _logicaAnio,
          "campo": "anio",
          "valor": _anioController.text.trim()
        });
      }
    }

    final body = {
      "titulo": _tituloController.text.trim(),
      "filtros": filtros,
    };

    try {
      final response = await http.post(
        Uri.parse('$baseUrl/api/canciones/busqueda'),
        body: jsonEncode(body),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final List<dynamic> listaJson = jsonDecode(response.body);
        setState(() {
          _resultados =
              listaJson.map((json) => Cancion.fromJson(json)).toList();
        });
      } else {
        throw Exception("Error de servidor");
      }
    } catch (e) {
      debugPrint("Error: $e");
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Error al buscar canciones")),
        );
      }
    } finally {
      setState(() => _isLoading = false);
    }
  }



  Widget _buildCampoConLogica({
    required String label,
    required TextEditingController controller,
    required String logica,
    required ValueChanged<String?> onLogicaChanged,
  }) {
    return Row(
      children: [
        DropdownButton<String>(
          value: logica,
          items: const [
            DropdownMenuItem(value: "NINGUNO", child: Text("—")),
            DropdownMenuItem(value: "AND", child: Text("AND")),
            DropdownMenuItem(value: "OR", child: Text("OR")),
          ],
          onChanged: onLogicaChanged,
        ),
        const SizedBox(width: 10),
        Expanded(
          child: TextField(
            controller: controller,
            decoration: InputDecoration(
              labelText: label,
              filled: true,
              fillColor: Colors.grey.shade100,
              border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
            ),
          ),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      resizeToAvoidBottomInset: false,
      appBar: AppBar(title: const Text("Buscar canciones")),
      body: Column(
        children: [
          Expanded(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: ListView(
                children: [
                  // --- Título con autocompletado ---
                  /*
                  Autocomplete<String>(
                    optionsBuilder: (TextEditingValue textEditingValue) {
                      _buscarSugerencias(textEditingValue.text);
                      return _sugerencias.where(
                        (s) => s
                            .toLowerCase()
                            .contains(textEditingValue.text.toLowerCase()),
                      );
                    },
                    fieldViewBuilder:
                        (context, controller, focusNode, onEditingComplete) {
                      _tituloController.text = controller.text;
                      return TextField(
                        controller: controller,
                        focusNode: focusNode,
                        onEditingComplete: onEditingComplete,
                        decoration: InputDecoration(
                          labelText: "Título de la canción",
                          filled: true,
                          fillColor: Colors.grey.shade100,
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(10),
                          ),
                        ),
                      );
                    },
                    onSelected: (String selection) {
                      _tituloController.text = selection;
                      _buscarCanciones();
                    },
                  ),
*/

                  Autocomplete<String>(
                    optionsBuilder: (TextEditingValue textEditingValue) {
                      final query = textEditingValue.text;
                      if (query.isEmpty) {
                        // Limpiar sugerencias si no hay texto
                        _sugerencias = [];
                        return const Iterable<String>.empty();
                      }

                      // Llamar al servidor solo si hay al menos 2 caracteres
                      if (query.length >= 2) {
                        _buscarSugerencias(query);
                      }

                      return _sugerencias.where(
                        (s) => s.toLowerCase().contains(query.toLowerCase()),
                      );
                    },
                    fieldViewBuilder: (context, controller, focusNode, onEditingComplete) {
                      return TextField(
                        controller: controller,
                        focusNode: focusNode,
                        onEditingComplete: onEditingComplete,
                        decoration: InputDecoration(
                          labelText: "Título de la canción",
                          filled: true,
                          fillColor: Colors.grey.shade100,
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(10),
                          ),
                        ),
                      );
                    },
                    onSelected: (String selection) {
                      _tituloController.text = selection;
                      _buscarCanciones();
                    },
                  ),
                  const SizedBox(height: 12),

                  SwitchListTile(
                    title: const Text("Búsqueda avanzada"),
                    value: _busquedaAvanzada,
                    onChanged: (value) {
                      setState(() => _busquedaAvanzada = value);
                    },
                  ),

                  if (_busquedaAvanzada) ...[
                    _buildCampoConLogica(
                      label: "Artista",
                      controller: _artistaController,
                      logica: _logicaArtista,
                      onLogicaChanged: (v) =>
                          setState(() => _logicaArtista = v ?? "NINGUNO"),
                    ),
                    const SizedBox(height: 10),
                    _buildCampoConLogica(
                      label: "Género",
                      controller: _generoController,
                      logica: _logicaGenero,
                      onLogicaChanged: (v) =>
                          setState(() => _logicaGenero = v ?? "NINGUNO"),
                    ),
                    const SizedBox(height: 10),
                    _buildCampoConLogica(
                      label: "Año",
                      controller: _anioController,
                      logica: _logicaAnio,
                      onLogicaChanged: (v) =>
                          setState(() => _logicaAnio = v ?? "NINGUNO"),
                    ),
                  ],

                  const SizedBox(height: 20),
                  ElevatedButton(
                    onPressed: _buscarCanciones,
                    child: const Text("🔍 Buscar"),
                  ),
                  const SizedBox(height: 20),

                  if (_isLoading)
                    const Center(child: CircularProgressIndicator()),
                  if (!_isLoading && _resultados.isEmpty)
                    const Center(child: Text("No se encontraron resultados")),

                  ..._resultados.map((cancion) => ListTile(
                        title: Text(cancion.titulo),
                        subtitle: Text(
                            "${cancion.artista} • ${cancion.genero} • ${cancion.anio}"),
                        onTap: () {
                          _audioService.playSong(
                              cancion);
                              
                        },
                      )),
                ],
              ),
            ),
          ),

          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            child: AudioPlayerWidget(),
          ),
        ],
      ),
    );
  }
}
