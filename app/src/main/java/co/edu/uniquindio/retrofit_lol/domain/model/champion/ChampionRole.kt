package co.edu.uniquindio.retrofit_lol.domain.model.champion

/**
 Roles (clases) de un campeón. [apiTag] es el texto exacto que devuelve la API en el arreglo "tags".
 */
enum class ChampionRole(val apiTag: String) {
    FIGHTER("Fighter"),
    TANK("Tank"),
    MAGE("Mage"),
    ASSASSIN("Assassin"),
    MARKSMAN("Marksman"),
    SUPPORT("Support");

    companion object {
        // Convierte el tag de la API en un rol; los tags desconocidos se ignoran (null).
        // Busca el rol que coincida con el texto de la API (sin importar mayúsculas/minúsculas) o devuelve null si no existe
        fun fromApiTag(tag: String): ChampionRole? = entries.firstOrNull { it.apiTag.equals(tag, ignoreCase = true) }
    }
}
