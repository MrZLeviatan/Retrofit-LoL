<h1 align="center">

_Núcleo de la Interfaz_

</h1>

La carpeta `core` agrupa lo que comparten todas las pantallas: el tema visual, los componentes de Compose reutilizables y utilidades de presentación. No contiene lógica de negocio ni acceso a datos.

<br>

## ¿Qué contiene?

### `theme/`

| Archivo | Descripción |
|---|---|
| `Color.kt` | Paleta inspirada en el cliente de League of Legends: dorado (`Gold`), hextech (`Hextech`), azules oscuros (`Abyss`, `Navy`) y los colores de estado `Online` y `Offline`. |
| `Theme.kt` | `RetrofitLoLTheme`: aplica un esquema de color oscuro fijo, como el cliente del juego. |
| `Type.kt` | Tipografía: títulos en serif y texto en sans-serif. |

<br>

### `component/`

Componentes de Compose sin estado propio del negocio: reciben datos y avisan eventos con lambdas.

**`card/`**

| Componente | Uso |
|---|---|
| `ChampionCard` | Fila de la lista: ícono (cargado con Coil), nombre, título, roles y dificultad. |

**`form/`**

| Componente | Uso |
|---|---|
| `SearchField` | Campo de búsqueda con lupa y botón para borrar. |
| `FormDropdown` | Selector desplegable genérico (`<T>`). Se usa para parche e idioma. |
| `AppChipRow` | Fila de chips con opción "Todos" (`null`). Se usa para el filtro por rol. |

**`indicator/`**

| Componente | Uso |
|---|---|
| `ConnectionIndicator` | Píldora con punto verde "En línea" o rojo "Sin conexión" en la barra superior. El color cambia con animación. |
| `OfflineBanner` | Franja roja que aparece debajo de la barra superior cuando no hay red. |
| `RatingBar` | Barra de 0 a 10 para ataque, defensa, magia y dificultad. |

**`text/`**

| Componente | Uso |
|---|---|
| `StatusMessage` | Mensaje centrado con ícono y botón opcional. Se usa para errores ("Reintentar") y listas vacías. |
| `SectionTitle` | Título dorado de cada sección del detalle. |
| `RoleTag` | Etiqueta con el nombre del rol (Mago, Tanque…). |

<br>

### `util/`

| Archivo | Descripción |
|---|---|
| `UiLabels.kt` | Traduce valores del dominio a textos de `strings.xml`: `ChampionRole.labelRes()` (rol → texto), `Throwable.toUserMessage()` (tipo de `ApiException` → mensaje) y `languageDisplayName()` (`es_MX` → "Español (México)"). |

El dominio no conoce `strings.xml`; esta carpeta hace esa traducción para que los textos se puedan cambiar o traducir sin tocar la lógica.
