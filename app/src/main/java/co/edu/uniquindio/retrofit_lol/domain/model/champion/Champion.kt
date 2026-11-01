package co.edu.uniquindio.retrofit_lol.domain.model.champion

/**
 Información básica de un campeón: es lo que se muestra en cada fila de la lista.
 */
data class Champion(
    // Identificador de la API (ej. "MonkeyKing"): se usa para pedir el detalle.
    val id: String,
    // Nombre visible (ej. "Wukong").
    val name: String,
    // Epíteto del campeón (ej. "el Rey Mono").
    val title: String,
    // Resumen corto de su historia.
    val blurb: String,
    val roles: List<ChampionRole>,
    // Dificultad de 0 a 10.
    val difficulty: Int,
    val iconUrl: String,
)
