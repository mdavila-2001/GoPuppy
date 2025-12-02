# 🐾 GoPuppy - App de Paseo de Mascotas

**GoPuppy** es una aplicación móvil nativa para Android desarrollada con **Kotlin** y **Jetpack Compose**. Su objetivo es conectar a dueños de mascotas con paseadores calificados, gestionando dos roles distintos (Dueño y Paseador) dentro de una misma aplicación con experiencias de usuario personalizadas.

## 📱 Características del Sistema

La aplicación adapta su interfaz y funcionalidad según el rol del usuario:

### 🐶 Modo Dueño (Owner) - *Tema Verde*
* **Gestión de Mascotas:** Registro, edición y eliminación de perfiles de mascotas con fotos y notas.
* **Búsqueda de Paseadores:** Localización de paseadores cercanos mediante GPS.
* **Solicitud de Paseos:** Agendamiento de paseos definiendo fecha, hora y duración.
* **Monitoreo:** Visualización del estado del paseo (Pendiente, En Curso, Finalizado) y fotos de evidencia.
* **Feedback:** Sistema de calificación y reseñas para los paseadores.

### 🚶‍♂️ Modo Paseador (Walker) - *Tema Naranja*
* **Gestión de Disponibilidad:** Interruptor para activar/desactivar el estado "Trabajando".
* **Gestión de Solicitudes:** Aceptar o rechazar nuevas solicitudes de paseo.
* **Agenda:** Visualización de paseos programados y aceptados.
* **Ejecución del Paseo:** Control de inicio y fin del paseo, con envío de ubicación en tiempo real.
* **Evidencia:** Funcionalidad para subir fotos durante el paseo.

---

## 🛠️ Stack Tecnológico

El proyecto sigue una arquitectura **MVVM (Model-View-ViewModel)** y principios de **Clean Architecture** en la capa de datos.

* **Lenguaje:** Kotlin 100%
* **UI:** Jetpack Compose (Material Design 3)
* **Navegación:** Navigation Compose
* **Conexión a Datos (Red):** Retrofit 2 + OkHttp + Gson
* **Carga de Imágenes:** Coil
* **Mapas y Ubicación:** Google Maps SDK + Google Location Services
* **Inyección de Dependencias:** Manual (por ahora) / Hilt (planificado)

---

## 📂 Estructura del Proyecto

El código fuente se encuentra bajo el paquete `com.mdavila_2001.gopuppy` y está organizado de la siguiente manera:

```text
app/src/main/java/com/mdavila_2001/gopuppy
├── data                    # Capa de Datos (Backend & Lógica)
│   ├── remote
│   │   ├── models          # Data Classes (Auth, Pet, Walk, Walker) [DTOs]
│   │   └── network         # Configuración de Retrofit y Endpoints (GoPuppyApiService)
│   └── repository          # Repositorios (AuthRepository, PetRepository, WalkRepository, WalkerRepository)
│
├── ui                      # Capa de Presentación (UI)
│   ├── components          # Componentes Reutilizables
│   │   └── global          # UI Kit (CamiDogButton, WalkerCard, StatusChip, etc.)
│   ├── navigation          # Grafo de Navegación (NavHost, Routes)
│   ├── screens             # Pantallas (Login, Home, Maps, Details)
│   └── theme               # Sistema de Diseño (Theme.kt, Color.kt)
│
└── MainActivity.kt         # Punto de entrada de la aplicación


🎨 Sistema de Diseño
La aplicación utiliza un sistema de temas dinámico (GoPuppyTheme).

Los componentes cambian de color automáticamente (Verde/Naranja) basándose en el rol del usuario logueado.

Soporte nativo para Modo Oscuro (Dark Mode) y Modo Claro (Light Mode).