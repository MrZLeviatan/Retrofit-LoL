package co.edu.uniquindio.retrofit_lol.domain.model.champion

/**
 Información completa de un campeón: la usa la pantalla de detalle.
 */
data class ChampionDetail(
    val id: String,
    val name: String,
    val title: String,
    val lore: String,
    val roles: List<ChampionRole>,
    // Recurso que gasta al usar habilidades (Maná, Energía, Furia...).
    val resource: String,
    val splashUrl: String,
    val info: ChampionInfo,
    val stats: ChampionStats,
    val passive: Ability,
    val spells: List<Ability>,
    val skins: List<Skin>,
    val allyTips: List<String>,
    val enemyTips: List<String>,
)

/** Valoraciones de 0 a 10 que da Riot a cada campeón. */
data class ChampionInfo(
    val attack: Int,
    val defense: Int,
    val magic: Int,
    val difficulty: Int,
)

/** Estadístico base del campeón en nivel 1. */
data class ChampionStats(
    val hp: Double,
    val mana: Double,
    val armor: Double,
    val magicResist: Double,
    val attackDamage: Double,
    val attackSpeed: Double,
    val moveSpeed: Double,
    val attackRange: Double,
)

/** Pasiva o habilidad (Q, W, E, R). */
data class Ability(
    // "P" para la pasiva; "Q", "W", "E" o "R" para las habilidades.
    val key: String,
    val name: String,
    val description: String,
    // Enfriamiento por nivel (ej. "7/6.5/6"); vacío en la pasiva.
    val cooldown: String,
    val iconUrl: String,
)

/** Aspecto (skin) del campeón con su imagen completa. */
data class Skin(
    val name: String,
    val splashUrl: String,
)
