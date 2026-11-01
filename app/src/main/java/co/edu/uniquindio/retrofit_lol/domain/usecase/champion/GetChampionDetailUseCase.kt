package co.edu.uniquindio.retrofit_lol.domain.usecase.champion

import co.edu.uniquindio.retrofit_lol.domain.model.catalog.GameDataSource
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionDetail
import co.edu.uniquindio.retrofit_lol.domain.repository.ChampionRepository

/**
 Caso de uso que trae el detalle completo de un campeón.
 */
class GetChampionDetailUseCase(

    // Inyecta el repositorio de campeones.
    private val championRepository: ChampionRepository
) {
    suspend operator fun invoke(source: GameDataSource, championId: String): Result<ChampionDetail> =
        championRepository.getChampionDetail(source, championId)
}
