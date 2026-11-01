<h1 align="center">

_Inyección de Dependencias_

</h1>

La carpeta `di` contiene los módulos de **Hilt**. Un módulo le dice a Hilt cómo crear un objeto que no puede construir solo (por ejemplo, Retrofit) o qué implementación usar para una interfaz.

Gracias a estos módulos, ninguna clase crea sus dependencias con `new`/constructores: las recibe ya armadas. Esto separa las capas y facilita las pruebas (en una prueba se puede pasar un `ApiService` falso).

Para que Hilt funcione, `LolApp` está anotada con `@HiltAndroidApp`, `MainActivity` con `@AndroidEntryPoint` y cada ViewModel con `@HiltViewModel`.

<br>

## Módulos

### `NetworkModule`

`object` instalado en `SingletonComponent`. Usa `@Provides` porque OkHttp y Retrofit se construyen con *builders*. Todo es `@Singleton`: una sola instancia para toda la app.

| Función | Crea |
|---|---|
| `provideOkHttpClient()` | Cliente HTTP con tiempos de espera (15 s conexión, 20 s lectura) y `HttpLoggingInterceptor`. El log solo se activa en debug (`BuildConfig.DEBUG`), porque en una versión publicada expondría datos de las peticiones y haría la app más lenta. |
| `provideRetrofit(client)` | Retrofit con la URL base de Data Dragon y `GsonConverterFactory` para convertir el JSON en DTOs. |
| `provideApiService(retrofit)` | La implementación de `ApiService` generada por `retrofit.create()`. |

<br>

### `RepositoryModule`

Clase abstracta instalada en `SingletonComponent`. Usa `@Binds` para enlazar cada interfaz del dominio con su implementación de `data/`.

| Interfaz (`domain`) | Implementación (`data`) |
|---|---|
| `ChampionRepository` | `ChampionRepositoryImpl` |
| `CatalogRepository` | `CatalogRepositoryImpl` |
| `ConnectivityRepository` | `NetworkConnectivityRepository` |

Son `@Singleton` para que la caché de campeones y catálogos se conserve mientras la app está abierta.

<br>

### `UseCaseModule`

`object` instalado en `ViewModelComponent`. Los casos de uso son clases simples de Kotlin sin anotaciones, así que se crean aquí con `@Provides`. Son `@ViewModelScoped`: viven lo mismo que el ViewModel que los usa.

Provee `GetChampionsPageUseCase`, `GetChampionDetailUseCase`, `GetVersionsUseCase`, `GetLanguagesUseCase` y `ObserveConnectivityUseCase`.

<br>

## Grafo de dependencias

```
OkHttpClient → Retrofit → ApiService → ChampionRepositoryImpl / CatalogRepositoryImpl
                                              ↓ (@Binds)
                                  ChampionRepository / CatalogRepository
                                              ↓
                                         Casos de uso → ViewModels
```
