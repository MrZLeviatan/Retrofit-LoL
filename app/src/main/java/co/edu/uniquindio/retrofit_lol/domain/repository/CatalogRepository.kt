package co.edu.uniquindio.retrofit_lol.domain.repository

/**
 Contrato para los catálogos de la API: parches e idiomas disponibles.
 */
interface CatalogRepository {

    // Parches del juego, del más reciente al más antiguo.
    suspend fun getVersions(): Result<List<String>>

    // Códigos de idioma disponibles (ej. "es_MX", "en_US").
    suspend fun getLanguages(): Result<List<String>>
}
