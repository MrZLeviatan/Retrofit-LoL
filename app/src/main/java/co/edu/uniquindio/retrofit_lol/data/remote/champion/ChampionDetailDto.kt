package co.edu.uniquindio.retrofit_lol.data.remote.champion

import co.edu.uniquindio.retrofit_lol.data.remote.DataDragonImages
import co.edu.uniquindio.retrofit_lol.domain.model.champion.Ability
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionDetail
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionInfo
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionRole
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionStats
import co.edu.uniquindio.retrofit_lol.domain.model.champion.Skin

/**
 Respuesta de champion/{id}.json. Igual que la lista, el campeón viene dentro de "data" con su id como llave.
 */
data class ChampionDetailResponseDto(
    val version: String = "",
    val data: Map<String, ChampionDetailDto> = emptyMap(),
)

data class ChampionDetailDto(
    val id: String = "",
    val name: String = "",
    val title: String = "",
    val lore: String = "",
    val tags: List<String> = emptyList(),
    val partype: String = "",
    val info: InfoDto = InfoDto(),
    val stats: StatsDto = StatsDto(),
    val spells: List<SpellDto> = emptyList(),
    val passive: PassiveDto = PassiveDto(),
    val skins: List<SkinDto> = emptyList(),
    val allytips: List<String> = emptyList(),
    val enemytips: List<String> = emptyList(),
) {
    fun toDomain(version: String) = ChampionDetail(
        id = id,
        name = name,
        title = title,
        lore = lore.cleanHtml(),
        roles = tags.mapNotNull(ChampionRole::fromApiTag),
        resource = partype,
        splashUrl = DataDragonImages.splash(id, 0),
        info = ChampionInfo(info.attack, info.defense, info.magic, info.difficulty),
        stats = stats.toDomain(),
        passive = Ability(
            key = "P",
            name = passive.name,
            description = passive.description.cleanHtml(),
            cooldown = "",
            iconUrl = DataDragonImages.passiveIcon(version, passive.image.full),
        ),
        // Las habilidades vienen en orden Q, W, E, R.
        spells = spells.mapIndexed { index, spell ->
            Ability(
                key = SPELL_KEYS.getOrElse(index) { "" },
                name = spell.name,
                description = spell.description.cleanHtml(),
                cooldown = spell.cooldownBurn,
                iconUrl = DataDragonImages.spellIcon(version, spell.image.full),
            )
        },
        skins = skins.map { skin ->
            // La skin 0 se llama "default" en la API: se muestra con el nombre del campeón.
            Skin(
                name = if (skin.num == 0) name else skin.name,
                splashUrl = DataDragonImages.splash(id, skin.num),
            )
        },
        allyTips = allytips.map { it.cleanHtml() },
        enemyTips = enemytips.map { it.cleanHtml() },
    )

    private companion object {
        val SPELL_KEYS = listOf("Q", "W", "E", "R")
    }
}

// Estadísticas base: la API usa nombres en minúscula y abreviados.
data class StatsDto(
    val hp: Double = 0.0,
    val mp: Double = 0.0,
    val armor: Double = 0.0,
    val spellblock: Double = 0.0,
    val attackdamage: Double = 0.0,
    val attackspeed: Double = 0.0,
    val movespeed: Double = 0.0,
    val attackrange: Double = 0.0,
) {
    fun toDomain() = ChampionStats(
        hp = hp,
        mana = mp,
        armor = armor,
        magicResist = spellblock,
        attackDamage = attackdamage,
        attackSpeed = attackspeed,
        moveSpeed = movespeed,
        attackRange = attackrange,
    )
}

data class SpellDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val cooldownBurn: String = "",
    val image: ImageDto = ImageDto(),
)

data class PassiveDto(
    val name: String = "",
    val description: String = "",
    val image: ImageDto = ImageDto(),
)

data class SkinDto(
    val num: Int = 0,
    val name: String = "",
)

// Las descripciones traen etiquetas HTML (<br>, <magicDamage>...): se dejan como texto plano.
private fun String.cleanHtml(): String =
    replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
        .replace(Regex("<[^>]+>"), "")
        .trim()
