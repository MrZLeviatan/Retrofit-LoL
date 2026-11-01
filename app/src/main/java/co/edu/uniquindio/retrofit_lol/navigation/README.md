<h1 align="center">

_Navegación_

</h1>

La carpeta `navigation` define las rutas de la aplicación y el grafo que las conecta, usando **Navigation Compose** con rutas tipadas (*type-safe*).

<br>

## Archivos

### `MainRoutes.kt`

Clase sellada `@Serializable` con las rutas. Al ser tipadas, los argumentos se pasan como propiedades de una clase en lugar de construir textos tipo `"detalle/{id}"`, y el compilador detecta errores.

| Ruta | Argumentos | Pantalla |
|---|---|---|
| `ChampionList` | — | Lista de campeones (destino inicial). |
| `ChampionDetail` | `championId`, `version`, `language` | Detalle de un campeón. |

El detalle recibe la versión y el idioma elegidos en la lista para consultar el endpoint de detalle con los mismos datos.

<br>

### `AppNavigation.kt`

Composable raíz con el `NavHost`:

- Crea el `NavController` con `rememberNavController()`.
- `ChampionList` recibe `onChampionClick` y navega a `ChampionDetail(championId, version, language)`.
- `ChampionDetail` recibe `onBack`, que llama a `popBackStack()` para volver a la lista.

Las pantallas no conocen el `NavController`: solo exponen lambdas (`onChampionClick`, `onBack`). Así la navegación queda centralizada en este archivo y las pantallas son más fáciles de reutilizar y probar.

Los argumentos de la ruta no se pasan a la pantalla: el `ChampionDetailViewModel` los lee directamente desde `SavedStateHandle` con `toRoute()`.
