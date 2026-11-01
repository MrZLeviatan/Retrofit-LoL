<h1 align="center">

_Capa de Datos_

</h1>

La capa `data` es la **fuente de datos** de la aplicación. Implementa los contratos definidos en `domain/repository` usando Retrofit para consumir la API Data Dragon y el `ConnectivityManager` de Android para conocer el estado de la red.

Es la única capa que conoce Retrofit, Gson y la forma del JSON. Hacia afuera solo entrega modelos del dominio.

<br>

## ¿Qué contiene?

### `remote/`

Todo lo relacionado con la API REST.

| Archivo | Descripción |
|---|---|
| `ApiService` | Interfaz de Retrofit con los 4 endpoints (`@GET` + `@Path`). Retrofit genera su implementación en tiempo de ejecución. Define `BASE_URL`. |
| `DataDragonImages` | Arma las URLs completas de las imágenes (ícono, splash art, habilidades y pasiva), porque la API solo devuelve el nombre del archivo. |
| `SafeApiCall` | Ejecuta una petición y convierte las excepciones en `ApiException`: `IOException` → `NoConnection`, `HttpException` 404 → `NotFound`, otros códigos → `Server`. Deja pasar la `CancellationException` de las corrutinas. |

**Endpoints de `ApiService`**

| Función | Endpoint | Retorno |
|---|---|---|
| `getVersions()` | `GET api/versions.json` | `List<String>` |
| `getLanguages()` | `GET cdn/languages.json` | `List<String>` |
| `getChampions(version, language)` | `GET cdn/{version}/data/{language}/champion.json` | `ChampionListResponseDto` |
| `getChampionDetail(version, language, championId)` | `GET cdn/{version}/data/{language}/champion/{championId}.json` | `ChampionDetailResponseDto` |

**`remote/champion/` — DTOs**

Los DTO (*Data Transfer Objects*) copian la forma del JSON. Todos sus campos tienen valores por defecto para que Gson no deje `null` si la API omite alguno. Cada DTO tiene `toDomain()`, que lo convierte en el modelo del dominio.

| Archivo | Clases |
|---|---|
| `ChampionDto.kt` | `ChampionListResponseDto` (el campo `data` es un mapa `id → campeón`), `ChampionDto`, `InfoDto`, `ImageDto`. |
| `ChampionDetailDto.kt` | `ChampionDetailResponseDto`, `ChampionDetailDto`, `StatsDto`, `SpellDto`, `PassiveDto`, `SkinDto`. Al convertir, limpia las etiquetas HTML de las descripciones (`<br>`, `<magicDamage>`…) y asigna las teclas Q, W, E, R. |

<br>

### `repository/remote/`

Implementaciones de los repositorios con Retrofit.

| Archivo | Descripción |
|---|---|
| `ChampionRepositoryImpl` | Data Dragon devuelve todos los campeones de una vez (no tiene `?page=`). El repositorio guarda la lista en memoria por versión e idioma, la ordena por nombre, aplica `ChampionFilter` y la entrega por páginas. Un `Mutex` evita pedir la misma lista dos veces al mismo tiempo. |
| `CatalogRepositoryImpl` | Pide versiones e idiomas y los guarda en memoria. De las versiones solo deja los parches con formato `x.y.z` (la API incluye entradas viejas como `lolpatch_3.7`) y se queda con los 15 más recientes. |

<br>

### `connectivity/`

| Archivo | Descripción |
|---|---|
| `NetworkConnectivityRepository` | Implementa `ConnectivityRepository`. Registra un `NetworkCallback` en el `ConnectivityManager` y lo expone como `Flow<Boolean>` con `callbackFlow`. Emite el estado inicial, se actualiza cuando la red aparece, se pierde o cambia, y quita el callback cuando nadie observa el Flow. |

<br>

## Flujo de una petición

```
ViewModel → UseCase → ChampionRepositoryImpl
                          └─ safeApiCall { api.getChampions(...) }
                                 └─ Retrofit + OkHttp → GET champion.json
                                 └─ Gson → ChampionListResponseDto
                          └─ dto.toDomain() → List<Champion>
                          └─ filtro + página → Result<ChampionPage>
```
