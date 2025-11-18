import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:fl_chart/fl_chart.dart';
import 'package:syncup_music_app/constants/constants.dart';

class ScreenAdminMetricas extends StatefulWidget {
  const ScreenAdminMetricas({super.key});

  @override
  State<ScreenAdminMetricas> createState() => _ScreenAdminMetricasState();
}

class _ScreenAdminMetricasState extends State<ScreenAdminMetricas> {
  Map<String, int> cancionesPopulares = {};
  Map<String, int> artistasPopulares = {};
  bool cargando = true;

  @override
  void initState() {
    super.initState();
    _cargarMetricas();
  }

  Future<void> _cargarMetricas() async {
    try {
      final urlCanciones = Uri.parse('$baseUrl/api/metricas/populares');
      final urlArtistas = Uri.parse('$baseUrl/api/metricas/artistas_populares');

      final resCanciones = await http.get(urlCanciones);
      final resArtistas = await http.get(urlArtistas);

      if (resCanciones.statusCode == 200 && resArtistas.statusCode == 200) {
        setState(() {
          cancionesPopulares =
              _mapearLista(json.decode(utf8.decode(resCanciones.bodyBytes)));
          artistasPopulares =
              _mapearLista(json.decode(utf8.decode(resArtistas.bodyBytes)));
          cargando = false;
        });
      } else {
        setState(() => cargando = false);
      }
    } catch (e) {
      setState(() => cargando = false);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error al cargar métricas: $e')),
      );
    }
  }

  // 🔹 Limpieza de los nombres recibidos del backend
  Map<String, int> _mapearLista(List<dynamic> data) {
    final map = <String, int>{};
    for (var item in data) {
      var key = item['key']?.toString() ?? 'Desconocido';
      key = key
          .replaceAll('.mp3', '')
          .replaceAll(RegExp(r'\s+'), ' ')
          .trim();
      if (key.contains('-')) key = key.split('-').first.trim();
      final value = int.tryParse(item['value'].toString()) ?? 0;
      map[key] = value;
    }
    return map;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Métricas del Sistema',
            style: TextStyle(fontWeight: FontWeight.bold)),
      ),
      body: cargando
          ? const Center(child: CircularProgressIndicator())
          : SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  const Text(
                    '🎵 Canciones más populares',
                    style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 12),
                  _buildPieChart(cancionesPopulares),
                  const SizedBox(height: 40),
                  const Text(
                    '👩‍🎤 Artistas más escuchados',
                    style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 12),
                  _buildBarChart(artistasPopulares),
                ],
              ),
            ),
    );
  }

  // ============================================================
  // PIE CHART - Canciones populares (una canción por fila)
  // ============================================================
  Widget _buildPieChart(Map<String, int> data) {
    if (data.isEmpty) {
      return const Center(child: Text('No hay datos disponibles.'));
    }

    final total = data.values.fold<int>(0, (a, b) => a + b);
    final colors = [
      Colors.deepPurple.shade300,
      Colors.purpleAccent.shade100,
      Colors.pinkAccent.shade100,
      Colors.blueAccent.shade100,
      Colors.orangeAccent.shade100,
      Colors.greenAccent.shade100,
    ];

    return Column(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        SizedBox(
          height: 250,
          child: PieChart(
            PieChartData(
              sections: data.entries.toList().asMap().entries.map((entry) {
                final i = entry.key;
                final e = entry.value;
                final porcentaje = (e.value / total) * 100;
                return PieChartSectionData(
                  color: colors[i % colors.length],
                  value: e.value.toDouble(),
                  title: '${porcentaje.toStringAsFixed(1)}%',
                  radius: 70,
                  titleStyle:
                      const TextStyle(fontSize: 12, color: Colors.black),
                );
              }).toList(),
              sectionsSpace: 2,
              centerSpaceRadius: 40,
            ),
          ),
        ),
        const SizedBox(height: 15),
        // 🔹 Leyenda (una canción por fila)
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: data.entries.toList().asMap().entries.map((entry) {
            final i = entry.key;
            final e = entry.value;
            return Padding(
              padding: const EdgeInsets.symmetric(vertical: 4, horizontal: 12),
              child: Row(
                children: [
                  Container(
                    width: 14,
                    height: 14,
                    color: colors[i % colors.length],
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      e.key.length > 40 ? '${e.key.substring(0, 40)}…' : e.key,
                      style: const TextStyle(fontSize: 13),
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                ],
              ),
            );
          }).toList(),
        ),
      ],
    );
  }


  // ============================================================
  // BARRAS HORIZONTALES - Artistas más escuchados (limpio)
  // ============================================================
  Widget _buildBarChart(Map<String, int> data) {
    if (data.isEmpty) {
      return const Center(child: Text('No hay datos disponibles.'));
    }

    final maxValor = data.values.reduce((a, b) => a > b ? a : b).toDouble();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: data.entries.map((entry) {
        final artista = entry.key;
        final valor = entry.value.toDouble();
        final proporcion = valor / maxValor;

        return Padding(
          padding: const EdgeInsets.symmetric(vertical: 6),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                artista.length > 40 ? '${artista.substring(0, 40)}…' : artista,
                style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w500),
              ),
              const SizedBox(height: 6),
              Stack(
                children: [
                  Container(
                    height: 18,
                    decoration: BoxDecoration(
                      color: Colors.grey.shade200,
                      borderRadius: BorderRadius.circular(10),
                    ),
                  ),
                  FractionallySizedBox(
                    widthFactor: proporcion,
                    child: Container(
                      height: 18,
                      decoration: BoxDecoration(
                        color: Colors.deepPurple.shade300,
                        borderRadius: BorderRadius.circular(10),
                      ),
                    ),
                  ),
                  Positioned.fill(
                    child: Align(
                      alignment: Alignment.centerRight,
                      child: Padding(
                        padding: const EdgeInsets.only(right: 6),
                        child: Text(
                          valor.toInt().toString(),
                          style: const TextStyle(
                            fontSize: 12,
                            color: Colors.black,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
        );
      }).toList(),
    );
  }


}
