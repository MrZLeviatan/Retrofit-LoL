package co.edu.uniquindio.retrofit_lol.domain.usecase.catalog

import co.edu.uniquindio.retrofit_lol.domain.repository.CatalogRepository

/**
 Caso de uso que trae los parches disponibles (filtro por versión).
 */
class GetVersionsUseCase(

    // Inyecta el repositorio de catálogos para consultar las fuentes de datos remotas o locales
    private val catalogRepository: CatalogRepository
) {
    // Define una función suspendida con el operador 'invoke' para ejecutar el caso de uso llamándolo directamente como función,
    // el cual llama al repositorio y devuelve un objeto Result con la lista de strings de las versiones disponibles
    suspend operator fun invoke(): Result<List<String>> = catalogRepository.getVersions()
}
