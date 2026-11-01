package co.edu.uniquindio.retrofit_lol.data.repository.remote

import co.edu.uniquindio.retrofit_lol.data.remote.ApiService
import co.edu.uniquindio.retrofit_lol.data.remote.safeApiCall
import co.edu.uniquindio.retrofit_lol.domain.repository.CatalogRepository
import jakarta.inject.Inject

/**
 Implementación con Retrofit del repositorio de catálogos.
 Los catálogos casi no cambian, así que se guardan en memoria tras la primera respuesta.
 */
class CatalogRepositoryImpl @Inject constructor(
    private val api: ApiService
) : CatalogRepository {

    private var versions: List<String>? = null
    private var languages: List<String>? = null

    override suspend fun getVersions(): Result<List<String>> = safeApiCall {
        versions ?: api.getVersions()
            // La lista trae entradas viejas como "lolpatch_3.7": solo se dejan los parches "x.y.z".
            .filter { it.matches(VERSION_PATTERN) }
            .take(MAX_VERSIONS)
            .also { versions = it }
    }

    override suspend fun getLanguages(): Result<List<String>> = safeApiCall {
        languages ?: api.getLanguages().sorted().also { languages = it }
    }

    private companion object {
        val VERSION_PATTERN = Regex("""\d+\.\d+\.\d+""")
        // Solo los parches más recientes, para que el selector no sea eterno.
        const val MAX_VERSIONS = 15
    }
}
