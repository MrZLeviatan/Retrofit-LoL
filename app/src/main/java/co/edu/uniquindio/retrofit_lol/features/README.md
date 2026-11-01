<h1 align="center">

_Funcionalidades (Pantallas)_

</h1>

La carpeta `features` contiene las pantallas de la aplicación. Cada funcionalidad tiene su propia carpeta con dos archivos:

- **`...Screen.kt`**: la interfaz en Jetpack Compose. Obtiene el ViewModel con `hiltViewModel()`, observa su estado con `collectAsStateWithLifecycle()` y se lo pasa a una versión sin estado (`...Content`) que solo dibuja.
- **`...ViewModel.kt`**: gestiona el estado de la pantalla con un `StateFlow<UiState>` y coordina los casos de uso. Sobrevive a los cambios de configuración (por ejemplo, rotar la pantalla).

El estado se agrupa en una sola `data class UiState`, así la pantalla siempre puede distinguir entre "cargando", "error", "lista vacía" y "con datos".

<br>

## `champion/list/` — Lista de campeones

### `ChampionListViewModel`

**Estado (`ChampionListUiState`)**: lista de campeones, texto de búsqueda, rol, parches e idiomas disponibles y seleccionados, banderas de carga (`isLoading` para la primera página e `isLoadingMore` para las siguientes), `canLoadMore`, total de resultados, errores (`error` y `loadMoreError`) y si hay conexión.

**Comportamiento**:

- Al iniciar pide parches e idiomas en paralelo (`async`) y después la primera página.
- `onQueryChange()` espera 300 ms (*debounce*) antes de recargar, para no filtrar con cada letra.
- `onRoleSelected()`, `onVersionSelected()` y `onLanguageSelected()` recargan desde la página 0.
- Cada recarga cancela la petición anterior (`loadJob`) para no mezclar resultados de filtros distintos.
- `loadNextPage()` pide la página siguiente y la agrega al final de la lista. No hace nada si ya está cargando o no quedan páginas.
- Observa la conexión: cuando la red vuelve, reintenta automáticamente lo que había fallado.

### `ChampionListScreen`

- Barra superior con el indicador de conexión y la franja de "sin conexión".
- Filtros: búsqueda por texto, selectores de parche e idioma y chips de rol.
- **Scroll infinito**: un `derivedStateOf` detecta cuando el último elemento visible está a 3 o menos del final y un `LaunchedEffect` llama a `loadNextPage()`.
- Pie de la lista: indicador de carga, error de página con "Reintentar" o "Llegaste al final de la lista".
- Al tocar un campeón navega al detalle con el parche y el idioma elegidos.

<br>

## `champion/detail/` — Detalle del campeón

### `ChampionDetailViewModel`

- Lee los argumentos de la ruta (`championId`, `version`, `language`) desde `SavedStateHandle` con `toRoute<MainRoutes.ChampionDetail>()`.
- Pide el detalle con `GetChampionDetailUseCase` (endpoint `champion/{id}.json`).
- Si falló por falta de red, lo vuelve a pedir solo cuando la conexión regresa.

### `ChampionDetailScreen`

Muestra, en un `LazyColumn`:

1. Splash art con degradado, nombre, título y roles.
2. Valoraciones (ataque, defensa, magia, dificultad).
3. Estadísticas base (vida, recurso, armadura, resistencia mágica, daño, velocidades y alcance).
4. Pasiva y habilidades Q, W, E, R con ícono, enfriamiento y descripción.
5. Carrusel horizontal de aspectos (skins).
6. Historia (lore) y consejos para jugarlo y para enfrentarlo.
