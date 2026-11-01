package co.edu.uniquindio.retrofit_lol.data.remote

import co.edu.uniquindio.retrofit_lol.data.remote.champion.ChampionDetailResponseDto
import co.edu.uniquindio.retrofit_lol.data.remote.champion.ChampionListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 Endpoints de Data Dragon, la API pública de Riot Games con los datos de League of Legends.
 Todos son GET, no piden API key y responden JSON.
 Base URL: https://ddragon.leagueoflegends.com/
 */
interface ApiService {

    // 1. Lista de parches del juego, del más nuevo al más viejo: ["16.21.1", "16.20.1", ...]
    @GET("api/versions.json")
    suspend fun getVersions(): List<String>

    // 2. Lista de idiomas disponibles: ["en_US", "es_MX", ...]
    @GET("cdn/languages.json")
    suspend fun getLanguages(): List<String>

    // 3. Todos los campeones de un parche en un idioma (información básica).
    @GET("cdn/{version}/data/{language}/champion.json")
    suspend fun getChampions(
        @Path("version") version: String,
        @Path("language") language: String,
    ): ChampionListResponseDto

    // 4. Detalle completo de un campeón (historia, habilidades, skins, estadísticas).
    @GET("cdn/{version}/data/{language}/champion/{championId}.json")
    suspend fun getChampionDetail(
        @Path("version") version: String,
        @Path("language") language: String,
        @Path("championId") championId: String,
    ): ChampionDetailResponseDto

    companion object {
        const val BASE_URL = "https://ddragon.leagueoflegends.com/"
    }
}
