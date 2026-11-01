package co.edu.uniquindio.retrofit_lol.domain.repository

import co.edu.uniquindio.retrofit_lol.domain.model.catalog.GameDataSource
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionDetail
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionFilter
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionPage

/**
 Contrato de acceso a los campeones. El dominio no sabe si vienen de Retrofit, de caché o de memoria.
 */
interface ChampionRepository {

    // Devuelve una página de campeones que cumplen el filtro.
    suspend fun getChampions(
        source: GameDataSource,
        filter: ChampionFilter,
        page: Int,
        pageSize: Int,
    ): Result<ChampionPage>

    // Devuelve la información completa de un campeón.
    suspend fun getChampionDetail(source: GameDataSource, championId: String): Result<ChampionDetail>
}
