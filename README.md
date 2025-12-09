# 🎵 Proyecto **SyncUp Music**
###  Plataforma de gestión y reproducción musical  

---

## 🧩 Descripción General  
SyncUp Music es una aplicación compuesta por un **backend en Java (Spring Boot)** y un **cliente móvil Android construido en Flutter**, el sistema permite gestionar canciones, listas de reproducción, filtros avanzados de búsqueda y manejo de favoritos.

Este proyecto integra estructuras de datos propias basadas en nodos y listas enlazadas (en java) donde el servidor controla la parte lógica de todas las tareas, el cliente realiza las peticiones y por medio de apis se comunican las 2 partes. 

---

# 🚀 Tecnologías Utilizadas  

---

# 🖥️ Backend – Java / Spring Boot

### 🔧 Lenguaje y Framework
- **Java 17 (JDK 17.0.8)**  
- **Spring Boot 3.x**  
- **Maven** como gestor de dependencias  

### 📦 Módulos y librerías principales
### 🖥️ Backend – Java 17 + Spring Boot 3.5.6

| Componente | Version | Funcion |
|-----------|---------|---------|
| **Java** | 17 | Lenguaje base del servidor |
| **Spring Boot** | 3.5.6 | Framework principal del backend |
| **Spring Web** | Incluido | API REST, controladores, manejo HTTP |
| **Spring Actuator** | Incluido | Metricas, monitoreo y endpoints de salud |
| **Spring WebSocket** | Incluido | Comunicacion en tiempo real |
| **Lombok** | Incluido | Reduce boilerplate (getters, setters, builders) |
| **Jaudiotagger** | 2.0.1 | Lectura de metadatos MP3/FLAC/WAV |
| **Spring Boot Test** | Incluido | JUnit, Mockito y AssertJ para pruebas |

### 📱 Frontend – Flutter 3.x + Dart 3.9.2

| Componente | Version | Funcion |
|-----------|---------|---------|
| **Flutter** | 3.x | Framework principal de la app movil |
| **Dart** | 3.9.2 | Lenguaje de programacion |
| **path_provider** | 2.0.15 | Acceso al sistema de archivos |
| **permission_handler** | 11.3.0 | Solicitud y manejo de permisos (almacenamiento, audio, etc.) |
| **http** | 0.13.6 | Consumo de API REST |
| **just_audio** | 0.9.36 | Reproduccion de audio avanzada |
| **audioplayers** | 5.2.1 | Utilidades adicionales de audio (notificaciones, sonidos cortos) |
| **shared_preferences** | 2.2.2 | Almacenamiento local ligero (favoritos, configuraciones) |
| **fl_chart** | 0.68.0 | Graficas y visualizacion de datos |
| **flutter_test** | Incluido | Testing nativo para Flutter |
| **flutter_lints** | 5.0.0 | Reglas de estilo y buenas practicas |
| **Material Design** | Incluido | Sistema visual base de la app |



## 📸 Vistas de la Aplicación (Usuario y Administrador)

### 1️⃣ Pantalla de Inicio – Registro y Login
<p align="center">
  <img src="https://drive.google.com/uc?export=view&id=1nGtcZJurhi_MSfwRw5XD6gK3vK6Yjd27" width="350">
</p>
Vista inicial donde el usuario puede crear una cuenta o iniciar sesión. Interfaz limpia y enfocada en el acceso rápido y seguro a la plataforma.

---

### 2️⃣ Búsqueda de Canciones
<p align="center">
  <img src="https://drive.google.com/uc?export=view&id=1WXlo8DX46dSVyJ9i_FNRkOFf9WeZBKZK" width="350">
</p>
Permite localizar rápidamente canciones mediante filtros y resultados dinámicos, explorando la música almacenada en el servidor.

---

### 3️⃣ Menú Principal del Usuario
<p align="center">
  <img src="https://drive.google.com/uc?export=view&id=16K_9w1M1bj092OII6VqyE2WcufLTHYXF" width="350">
</p>
Pantalla principal tras el inicio de sesión. Desde aquí el usuario puede acceder a distintas opciones como reproducir canciones, lista de favoritos, configuración.

---

### 4️⃣ Métricas del Sistema – Vista del Administrador
<p align="center">
  <img src="https://drive.google.com/uc?export=view&id=1TfybyAkHiL5KxSM0YHfTY_QwUxdcy96i" width="350">
</p>
Panel exclusivo para administradores donde se muestran estadísticas del servidor relaciuonadas con las canciones y artistas más escuchados.

---


