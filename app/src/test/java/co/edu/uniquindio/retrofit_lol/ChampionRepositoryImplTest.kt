package co.edu.uniquindio.retrofit_lol

import co.edu.uniquindio.retrofit_lol.data.remote.ApiService
import co.edu.uniquindio.retrofit_lol.data.remote.champion.ChampionDetailResponseDto
import co.edu.uniquindio.retrofit_lol.data.remote.champion.ChampionListResponseDto
import co.edu.uniquindio.retrofit_lol.data.repository.remote.ChampionRepositoryImpl
import co.edu.uniquindio.retrofit_lol.domain.error.ApiException
import co.edu.uniquindio.retrofit_lol.domain.model.catalog.GameDataSource
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionFilter
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionRole
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/**
 Prueba el repositorio con un ApiService falso: el JSON se parsea con Gson igual que en Retrofit.
 */
class ChampionRepositoryImplTest {

    private val listJson = """
        {"version":"16.21.1","data":{
          "Ahri":{"id":"Ahri","name":"Ahri","title":"la Vastaya de Nueve Colas","tags":["Mage","Assassin"],"info":{"difficulty":5},"image":{"full":"Ahri.png"}},
          "Garen":{"id":"Garen","name":"Garen","title":"el Poder de Demacia","tags":["Fighter","Tank"],"info":{"difficulty":2},"image":{"full":"Garen.png"}},
          "Lux":{"id":"Lux","name":"Lux","title":"la Dama Luminosa","tags":["Mage","Support"],"info":{"difficulty":5},"image":{"full":"Lux.png"}},
          "Annie":{"id":"Annie","name":"Annie","title":"la Niña Oscura","tags":["Mage"],"image":{"full":"Annie.png"}}
        }}
    """.trimIndent()

    private val detailJson = """
        {"data":{"Ahri":{"id":"Ahri","name":"Ahri","title":"t","lore":"Línea 1<br>Línea 2","tags":["Mage"],
          "stats":{"hp":590,"spellblock":30},"spells":[{"name":"Orbe","description":"Inflige <magicDamage>daño</magicDamage>","cooldownBurn":"7","image":{"full":"AhriQ.png"}}],
          "passive":{"name":"Pasiva","image":{"full":"P.png"}},"skins":[{"num":0,"name":"default"},{"num":1,"name":"Ahri Dinástica"}]}}}
    """.trimIndent()

    private val source = GameDataSource("16.21.1", "es_MX")

    private class FakeApi(private val list: String, private val detail: String, var fail: Boolean = false) : ApiService {
        var listCalls = 0
        override suspend fun getVersions() = listOf("16.21.1")
        override suspend fun getLanguages() = listOf("es_MX")
        override suspend fun getChampions(version: String, language: String): ChampionListResponseDto {
            if (fail) throw IOException("sin red")
            listCalls++
            return Gson().fromJson(list, ChampionListResponseDto::class.java)
        }
        override suspend fun getChampionDetail(version: String, language: String, championId: String) =
            Gson().fromJson(detail, ChampionDetailResponseDto::class.java)
    }

    @Test
    fun pagesAreSortedAndCached() = runBlocking {
        val api = FakeApi(listJson, detailJson)
        val repo = ChampionRepositoryImpl(api)

        val first = repo.getChampions(source, ChampionFilter(), page = 0, pageSize = 3).getOrThrow()
        val second = repo.getChampions(source, ChampionFilter(), page = 1, pageSize = 3).getOrThrow()

        assertEquals(listOf("Ahri", "Annie", "Garen"), first.items.map { it.name })
        assertTrue(first.hasMore)
        assertEquals(listOf("Lux"), second.items.map { it.name })
        assertFalse(second.hasMore)
        assertEquals(4, first.totalResults)
        // La segunda página sale de la caché: solo una petición a la API.
        assertEquals(1, api.listCalls)
        assertEquals("https://ddragon.leagueoflegends.com/cdn/16.21.1/img/champion/Ahri.png", first.items[0].iconUrl)
    }

    @Test
    fun filtersByTextAndRole() = runBlocking {
        val repo = ChampionRepositoryImpl(FakeApi(listJson, detailJson))

        val mages = repo.getChampions(source, ChampionFilter(role = ChampionRole.MAGE), 0, 10).getOrThrow()
        val demacia = repo.getChampions(source, ChampionFilter(query = "demacia"), 0, 10).getOrThrow()
        val support = repo.getChampions(source, ChampionFilter(query = "lu", role = ChampionRole.SUPPORT), 0, 10).getOrThrow()

        assertEquals(listOf("Ahri", "Annie", "Lux"), mages.items.map { it.name })
        assertEquals(listOf("Garen"), demacia.items.map { it.name })
        assertEquals(listOf("Lux"), support.items.map { it.name })
    }

    @Test
    fun networkErrorBecomesNoConnection() = runBlocking {
        val repo = ChampionRepositoryImpl(FakeApi(listJson, detailJson, fail = true))
        val result = repo.getChampions(source, ChampionFilter(), 0, 10)
        assertTrue(result.exceptionOrNull() is ApiException.NoConnection)
    }

    @Test
    fun detailIsMappedAndHtmlCleaned() = runBlocking {
        val detail = ChampionRepositoryImpl(FakeApi(listJson, detailJson)).getChampionDetail(source, "Ahri").getOrThrow()

        assertEquals("Línea 1\nLínea 2", detail.lore)
        assertEquals("Inflige daño", detail.spells[0].description)
        assertEquals("Q", detail.spells[0].key)
        assertEquals(30.0, detail.stats.magicResist, 0.0)
        assertEquals(listOf("Ahri", "Ahri Dinástica"), detail.skins.map { it.name })
        assertEquals("https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Ahri_1.jpg", detail.skins[1].splashUrl)
    }
}
