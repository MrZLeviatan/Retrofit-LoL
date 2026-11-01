package co.edu.uniquindio.retrofit_lol.domain.usecase.champion

import co.edu.uniquindio.retrofit_lol.domain.model.catalog.GameDataSource
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionFilter
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionPage
import co.edu.uniquindio.retrofit_lol.domain.repository.ChampionRepository

/**
 Caso de uso que trae una página de campeones (para el scroll infinito).
 */
class GetChampionsPageUseCase(

    // Inyecta el repositorio de campeones.
    private val championRepository: ChampionRepository
) {
    suspend operator fun invoke(
        source: GameDataSource,
        filter: ChampionFilter,
        page: Int,
        pageSize: Int = PAGE_SIZE,
    ): Result<ChampionPage> = championRepository.getChampions(source, filter, page, pageSize)

    companion object {
        // Cantidad de campeones por página.
        const val PAGE_SIZE = 12
    }
}
