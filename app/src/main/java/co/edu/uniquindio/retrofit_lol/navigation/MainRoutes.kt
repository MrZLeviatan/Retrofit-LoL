package co.edu.uniquindio.retrofit_lol.navigation

import kotlinx.serialization.Serializable

// Agrupa las rutas de la app. @Serializable permite pasarlas a Navigation Compose como rutas tipadas.
@Serializable
sealed class MainRoutes {

    // Lista de campeones con búsqueda, filtros y scroll infinito.
    @Serializable
    data object ChampionList : MainRoutes()

    // Detalle de un campeón. Lleva la versión y el idioma elegidos en la lista
    // para pedir el detalle con los mismos datos.
    @Serializable
    data class ChampionDetail(
        val championId: String,
        val version: String,
        val language: String,
    ) : MainRoutes()
}
