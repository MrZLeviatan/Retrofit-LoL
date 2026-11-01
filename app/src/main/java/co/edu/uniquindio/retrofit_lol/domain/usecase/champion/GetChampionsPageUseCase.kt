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
    // Declara una función suspendida y sobrecarga el operador 'invoke' para ejecutar el caso de uso llamando directamente a la instancia como una función
    suspend operator fun invoke(
        // Parámetro que especifica la versión y el idioma a consultar en la API
        source: GameDataSource,
        // Parámetro con los criterios de filtrado aplicados (nombre/título y rol)
        filter: ChampionFilter,
        // Parámetro que indica el número de página actual que se va a solicitar
        page: Int,
        // Parámetro opcional que define la cantidad de elementos por página, tomando 12 por defecto
        pageSize: Int = PAGE_SIZE,
        // Llama al repositorio para obtener el resultado envuelto en un objeto Result (éxito o fallo)
    ): Result<ChampionPage> = championRepository.getChampions(source, filter, page, pageSize)

    // Contenedor estático para albergar constantes globales de esta clase
    companion object {
        // Constante que define el tamaño estándar de cada página para el scroll infinito
        const val PAGE_SIZE = 12
    }
}