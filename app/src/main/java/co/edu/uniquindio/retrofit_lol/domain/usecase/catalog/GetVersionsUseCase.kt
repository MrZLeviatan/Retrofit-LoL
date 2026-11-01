package co.edu.uniquindio.retrofit_lol.domain.usecase.catalog

import co.edu.uniquindio.retrofit_lol.domain.repository.CatalogRepository

/**
 Caso de uso que trae los parches disponibles (filtro por versión).
 */
class GetVersionsUseCase(

    // Inyecta el repositorio de catálogos.
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(): Result<List<String>> = catalogRepository.getVersions()
}
