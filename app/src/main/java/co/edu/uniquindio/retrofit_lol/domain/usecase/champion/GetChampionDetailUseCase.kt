package co.edu.uniquindio.retrofit_lol.domain.usecase.champion

import co.edu.uniquindio.retrofit_lol.domain.model.catalog.GameDataSource
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionDetail
import co.edu.uniquindio.retrofit_lol.domain.repository.ChampionRepository

/**
 Caso de uso que trae el detalle completo de un campeón.
 */
class GetChampionDetailUseCase(

    // Inyecta el repositorio de campeones para obtener los datos desde la fuente remota o local
    private val championRepository: ChampionRepository
) {
    // Declara una función suspendida y sobrecarga el operador 'invoke' para permitir ejecutar la clase directamente como si fuera una función
    suspend operator fun invoke(
        // Parámetro que especifica la versión del juego y el idioma con los que se consulta la API
        source: GameDataSource,
        // Parámetro único que identifica al campeón del cual se requiere el detalle
        championId: String,
        // Llama al repositorio para obtener el detalle completo del campeón envuelto en un objeto Result (éxito o fallo)
    ): Result<ChampionDetail> = championRepository.getChampionDetail(source, championId)
}