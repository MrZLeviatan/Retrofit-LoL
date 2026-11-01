<h1 align="center">

_Capa de Dominio_

</h1>

La capa `domain` contiene los conceptos centrales de la aplicación: qué es un campeón, qué información se muestra de él, qué errores pueden ocurrir y qué operaciones de datos necesita la app.

Es Kotlin puro: no conoce Retrofit, Gson, Compose ni Android. Así, si la fuente de datos cambia (otra API, una base de datos local), esta capa no se modifica.

<br>

## ¿Qué contiene?

### `model/`

Modelos del negocio, ya limpios y listos para la UI (sin la forma del JSON de la API).

**`champion/`**

| Archivo | Descripción |
|---|---|
| `Champion` | Información básica de un campeón para la lista: id, nombre, título, resumen, roles, dificultad y URL del ícono. |
| `ChampionDetail` | Información completa para el detalle. Incluye `ChampionInfo` (valoraciones de 0 a 10), `ChampionStats` (estadísticas base), `Ability` (pasiva y habilidades Q, W, E, R) y `Skin` (aspectos). |
| `ChampionRole` | Enum con los roles del juego. `apiTag` guarda el texto exacto que devuelve la API y `fromApiTag()` lo convierte. |
| `ChampionFilter` | Criterios de búsqueda (texto y rol). Su función `matches()` es la regla de negocio que decide si un campeón aparece en los resultados. |
| `ChampionPage` | Una página de resultados para el scroll infinito: elementos, número de página, total y si quedan más páginas. |

**`catalog/`**

| Archivo | Descripción |
|---|---|
| `GameDataSource` | Parche (versión) e idioma con los que se consultan los datos. Cambiar cualquiera de los dos cambia el endpoint. Define el idioma por defecto `es_MX`. |

<br>

### `error/`

`ApiException` es una clase sellada que traduce los fallos técnicos a casos que entiende el resto de la app:

- `NoConnection`: sin internet, timeout o DNS.
- `NotFound`: la API respondió 404 (por ejemplo, un idioma que no existe para ese parche).
- `Server(code)`: la API respondió con otro código de error HTTP.
- `Unknown`: cualquier otro problema, como un JSON con formato inesperado.

El dominio define el significado del error; la UI decide qué texto mostrar.

<br>

### `repository/`

Interfaces (contratos) que declaran **qué** datos necesita la app, sin decir **cómo** se obtienen. Las implementaciones están en `data/`.

| Interfaz | Operaciones |
|---|---|
| `ChampionRepository` | `getChampions(source, filter, page, pageSize)` devuelve una página filtrada; `getChampionDetail(source, championId)` devuelve el detalle. |
| `CatalogRepository` | `getVersions()` y `getLanguages()` para los filtros de parche e idioma. |
| `ConnectivityRepository` | `isOnline: Flow<Boolean>` emite el estado de la conexión. |

Todas las operaciones de red devuelven `Result<T>`, así quien las llama siempre debe manejar el caso de error.

<br>

### `usecase/`

Cada caso de uso representa una acción de la app y delega en un repositorio. Se invocan como funciones gracias a `operator fun invoke`.

| Carpeta | Caso de uso | Acción |
|---|---|---|
| `champion/` | `GetChampionsPageUseCase` | Trae una página de campeones (tamaño por defecto: 12). |
| `champion/` | `GetChampionDetailUseCase` | Trae el detalle de un campeón. |
| `catalog/` | `GetVersionsUseCase` | Trae los parches disponibles. |
| `catalog/` | `GetLanguagesUseCase` | Trae los idiomas disponibles. |
| `connectivity/` | `ObserveConnectivityUseCase` | Observa si el dispositivo está online u offline. |

Los casos de uso no tienen anotaciones de Hilt; se crean en `di/UseCaseModule`.
