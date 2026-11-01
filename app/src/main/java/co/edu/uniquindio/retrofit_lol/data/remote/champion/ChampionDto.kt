package co.edu.uniquindio.retrofit_lol.data.remote.champion

import co.edu.uniquindio.retrofit_lol.data.remote.DataDragonImages
import co.edu.uniquindio.retrofit_lol.domain.model.champion.Champion
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionRole

/**
 Respuesta de champion.json. Los campeones vienen en un objeto cuya llave es el id:
 { "version": "16.21.1", "data": { "Aatrox": { ... }, "Ahri": { ... } } }
 Los valores por defecto evitan nulls si la API omite algún campo.
 */
data class ChampionListResponseDto(
    val version: String = "",
    val data: Map<String, ChampionDto> = emptyMap(),
)

// Un campeón tal como viene en el JSON (solo los campos que usa la app).
data class ChampionDto(
    val id: String = "",
    val key: String = "",
    val name: String = "",
    val title: String = "",
    val blurb: String = "",
    val tags: List<String> = emptyList(),
    val info: InfoDto = InfoDto(),
    val image: ImageDto = ImageDto(),
) {
    // Convierte el DTO en el modelo del dominio.
    fun toDomain(version: String) = Champion(
        id = id,
        name = name,
        title = title,
        blurb = blurb,
        roles = tags.mapNotNull(ChampionRole::fromApiTag),
        difficulty = info.difficulty,
        iconUrl = DataDragonImages.championIcon(version, image.full),
    )
}

// Valoraciones de 0 a 10.
data class InfoDto(
    val attack: Int = 0,
    val defense: Int = 0,
    val magic: Int = 0,
    val difficulty: Int = 0,
)

// Datos de la imagen: solo se necesita el nombre del archivo.
data class ImageDto(
    val full: String = "",
)
