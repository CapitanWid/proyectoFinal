class Cancion {
  final String id;
  final String titulo;
  final String artista;
  final String genero;
  final String anio;
  final String? nombreArchivo;

  Cancion({
    required this.id,
    required this.titulo,
    required this.artista,
    required this.genero,
    required this.anio,
    this.nombreArchivo,
  });

  factory Cancion.fromJson(Map<String, dynamic> json) {
    return Cancion(
      id: json['id'] ?? '', // <- Nuevo campo que viene del servidor
      titulo: json['titulo'] ?? '',
      artista: json['artista'] ?? '',
      genero: json['genero'] ?? '',
      anio: json['anio'] ?? '',
      nombreArchivo: json['nombreArchivo'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'titulo': titulo,
      'artista': artista,
      'genero': genero,
      'anio': anio,
      'nombreArchivo': nombreArchivo,
    };
  }
}
