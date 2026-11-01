package co.edu.uniquindio.retrofit_lol.domain.model.champion

/**
 Criterios de búsqueda locales: texto (nombre o título) y rol.
 */
data class ChampionFilter(
    val query: String = "",
    val role: ChampionRole? = null,
) {
    // Regla de negocio: un campeón pasa si su nombre o título contiene el texto y tiene el rol elegido.
    fun matches(champion: Champion): Boolean {
        val text = query.trim()
        val matchesText = text.isEmpty() ||
            champion.name.contains(text, ignoreCase = true) ||
            champion.title.contains(text, ignoreCase = true)
        val matchesRole = role == null || role in champion.roles
        return matchesText && matchesRole
    }
}
