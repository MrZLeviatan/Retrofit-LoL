package co.edu.uniquindio.retrofit_lol.data.repository.remote

import co.edu.uniquindio.retrofit_lol.data.remote.ApiService
import co.edu.uniquindio.retrofit_lol.data.remote.safeApiCall
import co.edu.uniquindio.retrofit_lol.domain.error.ApiException
import co.edu.uniquindio.retrofit_lol.domain.model.catalog.GameDataSource
import co.edu.uniquindio.retrofit_lol.domain.model.champion.Champion
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionDetail
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionFilter
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionPage
import co.edu.uniquindio.retrofit_lol.domain.repository.ChampionRepository
import jakarta.inject.Inject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 Implementación con Retrofit del repositorio de campeones.

 Data Dragon devuelve todos los campeones de un parche en una sola respuesta (no tiene ?page=).
 Por eso el repositorio guarda esa respuesta en memoria por (versión, idioma) y la entrega
 por páginas: la UI no se entera y hace scroll infinito igual que con una API paginada.
 */
class ChampionRepositoryImpl @Inject constructor(
    private val api: ApiService
) : ChampionRepository {

    // Caché: "16.21.1/es_MX" -> lista completa ordenada por nombre.
    private val cache = mutableMapOf<String, List<Champion>>()
    // Evita que dos páginas pidan la misma lista al mismo tiempo.
    private val mutex = Mutex()

    override suspend fun getChampions(
        source: GameDataSource,
        filter: ChampionFilter,
        page: Int,
        pageSize: Int,
    ): Result<ChampionPage> = safeApiCall {
        val filtered = allChampions(source).filter(filter::matches)
        val from = page * pageSize
        ChampionPage(
            items = filtered.drop(from).take(pageSize),
            page = page,
            totalResults = filtered.size,
            hasMore = from + pageSize < filtered.size,
        )
    }

    override suspend fun getChampionDetail(
        source: GameDataSource,
        championId: String,
    ): Result<ChampionDetail> = safeApiCall {
        val response = api.getChampionDetail(source.version, source.language, championId)
        // El campeón viene dentro de "data" con su id como llave.
        val dto = response.data[championId] ?: response.data.values.firstOrNull()
            ?: throw ApiException.NotFound()
        dto.toDomain(source.version)
    }

    private suspend fun allChampions(source: GameDataSource): List<Champion> = mutex.withLock {
        val key = "${source.version}/${source.language}"
        cache.getOrPut(key) {
            api.getChampions(source.version, source.language)
                .data.values
                .map { it.toDomain(source.version) }
                .sortedBy { it.name }
        }
    }
}
