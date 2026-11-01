# Retrofit-LoL
 Aplicación Android nativa en Kotlin (Jetpack Compose) que consume la API REST pública Data Dragon de Riot Games para explorar los campeones de League of Legends: lista con búsqueda y filtros, detalle completo de cada campeón e indicador de conexión online/offline.

<br>

## 1. Entorno e introducción

### Presentación del grupo

| Integrante | Código | GitHub |
|---|---|---|
| _Nombre completo_ | _Código_ | [@usuario](https://github.com/usuario) |
| _Nombre completo_ | _Código_ | [@usuario](https://github.com/usuario) |

**Universidad del Quindío** — Desarrollo de Aplicaciones Móviles.
**Actividad:** Consumo de APIs REST en Android.

### ¿Qué hace la app?

Retrofit-LoL permite explorar los campeones de League of Legends con datos oficiales y actualizados de Riot Games:

- **Lista de campeones** con ícono, nombre, título, roles y dificultad.
- **Búsqueda y filtros**: por texto (nombre o título), por rol (Luchador, Tanque, Mago, Asesino, Tirador, Soporte), por parche del juego y por idioma.
- **Scroll infinito**: la lista se carga en páginas de 12 campeones a medida que el usuario baja.
- **Detalle del campeón**: splash art, valoraciones, estadísticas base, pasiva y habilidades (Q, W, E, R), aspectos, historia y consejos.
- **Indicador de conexión**: píldora "En línea / Sin conexión" y franja roja cuando no hay red; al volver la conexión se reintenta lo que falló.
- **Manejo de errores**: sin conexión, recurso no encontrado (404) y error del servidor, cada uno con su mensaje y botón **Reintentar**.

### Entorno de desarrollo

| Elemento | Versión / valor |
|---|---|
| IDE | Android Studio |
| Lenguaje | Kotlin 2.4.20 |
| JDK (toolchain de Gradle) | 25 |
| Gradle / Android Gradle Plugin | 9.6.0 / 9.4.1 |
| `compileSdk` / `targetSdk` | 37 |
| `minSdk` | 24 (Android 7.0) |
| UI | Jetpack Compose (BOM 2026.09.00) + Material 3 |

**Librerías principales**

| Librería | Uso |
|---|---|
| Retrofit 3.0.0 + Gson converter | Peticiones HTTP y conversión del JSON a clases Kotlin |
| OkHttp Logging Interceptor 4.12.0 | Log de peticiones en Logcat (solo en debug) |
| Hilt 2.60.1 + KSP | Inyección de dependencias |
| Navigation Compose 2.10.2 | Navegación con rutas tipadas (`@Serializable`) |
| Coil 3 | Carga de imágenes desde URL |
| Coroutines + StateFlow | Asincronía y estado de la UI |

**Permisos:** `INTERNET` (peticiones a la API e imágenes) y `ACCESS_NETWORK_STATE` (indicador online/offline).

**Ejecución**

1. Abrir la carpeta del proyecto en Android Studio y esperar la sincronización de Gradle.
2. Ejecutar la configuración `app` en un emulador o dispositivo con internet.

Desde la terminal también se puede compilar el APK y correr las pruebas unitarias:

```bash
./gradlew assembleDebug
```

```bash
./gradlew testDebugUnitTest
```

<br>

## 2. Descripción de la API

**Data Dragon** es el servicio público de Riot Games que publica los datos estáticos de League of Legends: campeones, habilidades, aspectos e imágenes, organizados por parche (versión del juego) e idioma. No requiere registro ni API key.

- **URL base:** `https://ddragon.leagueoflegends.com/`
- **Método HTTP:** todos los endpoints usan **GET** (solo lectura).
- **Formato de respuesta:** **JSON**.
- **Parámetros:** van en la ruta de la URL (`@Path` en Retrofit), no en query string.

### Endpoints utilizados

| # | Endpoint | Parámetros | Respuesta | Uso en la app |
|---|---|---|---|---|
| 1 | `GET api/versions.json` | — | Arreglo de strings con los parches, del más nuevo al más viejo | Filtro por parche |
| 2 | `GET cdn/languages.json` | — | Arreglo de strings con códigos de idioma | Filtro por idioma |
| 3 | `GET cdn/{version}/data/{language}/champion.json` | `version`, `language` | Objeto con `data`: mapa `id → campeón` con información básica | Lista de campeones |
| 4 | `GET cdn/{version}/data/{language}/champion/{championId}.json` | `version`, `language`, `championId` | Objeto con `data`: el campeón con historia, habilidades, aspectos y estadísticas | Pantalla de detalle |

### Parámetros

| Parámetro | Descripción | Ejemplo | De dónde sale |
|---|---|---|---|
| `version` | Parche del juego | `16.21.1` | Endpoint 1 |
| `language` | Código de idioma y región | `es_MX`, `en_US` | Endpoint 2 |
| `championId` | Identificador interno del campeón (puede diferir del nombre visible) | `Ahri`, `MonkeyKing` (Wukong) | Campo `id` del endpoint 3 |

### Ejemplos de respuesta

**Endpoint 1 — versiones**

```json
["16.21.1", "16.20.1", "16.19.1", "..."]
```

**Endpoint 3 — lista de campeones** (resumido)

```json
{
  "version": "16.21.1",
  "data": {
    "Ahri": {
      "id": "Ahri",
      "name": "Ahri",
      "title": "la Vastaya de Nueve Colas",
      "tags": ["Mage", "Assassin"],
      "info": { "attack": 3, "defense": 4, "magic": 8, "difficulty": 5 },
      "image": { "full": "Ahri.png" }
    }
  }
}
```

**Endpoint 4 — detalle** (resumido)

```json
{
  "data": {
    "Ahri": {
      "lore": "...",
      "stats": { "hp": 590, "armor": 21, "spellblock": 30, "movespeed": 330 },
      "passive": { "name": "Ladrona de Esencias", "image": { "full": "Ahri_SoulEater2.png" } },
      "spells": [{ "name": "Orbe del Engaño", "cooldownBurn": "7", "image": { "full": "AhriQ.png" } }],
      "skins": [{ "num": 0, "name": "default" }, { "num": 1, "name": "Ahri Dinástica" }]
    }
  }
}
```

### Imágenes

La API solo devuelve el nombre del archivo; la app construye la URL completa:

| Imagen | URL |
|---|---|
| Ícono del campeón | `cdn/{version}/img/champion/{image.full}` |
| Splash art (aspecto) | `cdn/img/champion/splash/{id}_{num}.jpg` |
| Habilidad | `cdn/{version}/img/spell/{image.full}` |
| Pasiva | `cdn/{version}/img/passive/{image.full}` |

### Búsqueda, filtros y paginación

- **Parche e idioma** cambian la URL del endpoint 3: cada combinación es una petición distinta.
- **Texto y rol** filtran sobre campos del JSON (`name`, `title` y `tags`).
- **Paginación:** Data Dragon devuelve todos los campeones de un parche en una sola respuesta (no tiene parámetro `page`). El repositorio guarda esa respuesta en memoria por versión e idioma y la entrega en páginas de 12, de modo que la UI implementa scroll infinito igual que con una API paginada.

<br>

## 3. Configuración de las carpetas

### Raíz del proyecto

```
Retrofit-LoL/
├── app/                     → Módulo de la aplicación
│   ├── build.gradle.kts     → Plugins, configuración Android y dependencias del módulo
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml   → Permisos, LolApp y MainActivity
│       │   ├── java/.../retrofit_lol/ → Código fuente (ver abajo)
│       │   ├── keepRules/rules.keep  → Reglas de R8 (conserva los DTOs para Gson)
│       │   └── res/                  → Textos (strings.xml), tema base e íconos
│       ├── test/            → Pruebas unitarias (ChampionRepositoryImplTest)
│       └── androidTest/     → Pruebas instrumentadas
├── gradle/
│   ├── libs.versions.toml   → Catálogo de versiones de librerías y plugins
│   └── wrapper/             → Gradle Wrapper
├── build.gradle.kts         → Plugins comunes del proyecto
├── settings.gradle.kts      → Repositorios y módulos incluidos
└── gradle.properties        → Opciones de Gradle
```

### Código fuente — `app/src/main/java/co/edu/uniquindio/retrofit_lol/`

El código usa una arquitectura en capas con inyección de dependencias (Hilt). Cada capa solo conoce a la de abajo:

```
features (UI + ViewModel) → domain (casos de uso) → domain (interfaz de repositorio)
                                                            ↑ implementada por
                                                  data (Retrofit → Data Dragon)
```

```
retrofit_lol/
├── LolApp.kt          → Application con @HiltAndroidApp
├── MainActivity.kt    → @AndroidEntryPoint; aplica el tema y carga la navegación
├── core/              → Tema, componentes reutilizables y utilidades de UI
├── data/              → Retrofit (ApiService), DTOs, repositorios y conectividad
├── di/                → Módulos de Hilt: red, repositorios y casos de uso
├── domain/            → Modelos, errores, contratos de repositorio y casos de uso
├── features/          → Pantallas (Compose) y ViewModels
└── navigation/        → Rutas tipadas y NavHost
```

| Carpeta | Capa | Responsabilidad | Detalle |
|---|---|---|---|
| `core/` | UI | Tema visual estilo LoL, componentes de Compose compartidos (tarjetas, filtros, indicador de conexión) y traducción de valores a textos | [core/README.md](app/src/main/java/co/edu/uniquindio/retrofit_lol/core/README.md) |
| `data/` | Fuente de datos | `ApiService` con los 4 endpoints, DTOs que copian el JSON y se convierten con `toDomain()`, repositorios con caché y paginación, observador de red | [data/README.md](app/src/main/java/co/edu/uniquindio/retrofit_lol/data/README.md) |
| `di/` | Inyección | `NetworkModule` (OkHttp, Retrofit, ApiService), `RepositoryModule` (interfaz → implementación) y `UseCaseModule` | [di/README.md](app/src/main/java/co/edu/uniquindio/retrofit_lol/di/README.md) |
| `domain/` | Dominio | Modelos del negocio, `ApiException`, interfaces de repositorio y casos de uso. Kotlin puro, sin Android ni Retrofit | [domain/README.md](app/src/main/java/co/edu/uniquindio/retrofit_lol/domain/README.md) |
| `features/` | UI + ViewModel | Lista de campeones (filtros y scroll infinito) y detalle; cada una con su `Screen` y su `ViewModel` con `StateFlow<UiState>` | [features/README.md](app/src/main/java/co/edu/uniquindio/retrofit_lol/features/README.md) |
| `navigation/` | UI | `MainRoutes` (rutas `@Serializable`) y `AppNavigation` (NavHost) | [navigation/README.md](app/src/main/java/co/edu/uniquindio/retrofit_lol/navigation/README.md) |
