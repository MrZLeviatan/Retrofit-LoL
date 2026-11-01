package co.edu.uniquindio.retrofit_lol.domain.model.champion

/**
 Criterios de búsqueda locales: texto (nombre o título) y rol.
 */
data class ChampionFilter(
    // Texto ingresado por el usuario para buscar (vacío por defecto)
    val query: String = "",
    // Filtro opcional por rol de campeón (nulo si no hay ningún rol seleccionado)
    val role: ChampionRole? = null,
) {
    // Regla de negocio: un campeón pasa si su nombre o título contiene el texto y tiene el rol elegido.
    fun matches(champion: Champion): Boolean {
        // Limpia los espacios en blanco sobrantes al inicio y final de la consulta de texto
        val text = query.trim()

        // Válida si el filtro de texto está vacío (pasan todos) o si coincide con el nombre/título
        val matchesText = text.isEmpty() ||
                // Verifica si el nombre del campeón contiene el texto (sin importar mayúsculas/minúsculas)
                champion.name.contains(text, ignoreCase = true) ||
                // Verifica si el título del campeón contiene dicho texto
                champion.title.contains(text, ignoreCase = true)

        // Válida si no hay filtro de rol (pasan todos) o si el rol buscado está dentro de los roles del campeón
        val matchesRole = role == null || role in champion.roles

        // El campeón cumple el filtro únicamente si coinciden tanto el criterio de texto como el de rol
        return matchesText && matchesRole
    }
}
