package co.edu.uniquindio.retrofit_lol.domain.model.champion

/**
 Una página de resultados para el scroll infinito.
 */
data class ChampionPage(
    val items: List<Champion>,
    // Número de página pedida (empieza en 0).
    val page: Int,
    // Total de campeones que cumplen el filtro (todas las páginas).
    val totalResults: Int,
    // true si todavía quedan páginas por cargar.
    val hasMore: Boolean,
)
