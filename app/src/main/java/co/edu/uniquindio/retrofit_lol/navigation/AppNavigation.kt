package co.edu.uniquindio.retrofit_lol.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.edu.uniquindio.retrofit_lol.features.champion.detail.ChampionDetailScreen
import co.edu.uniquindio.retrofit_lol.features.champion.list.ChampionListScreen

// Componente raíz de navegación encargado de configurar las rutas de la aplicación.
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainRoutes.ChampionList
    ) {

        composable<MainRoutes.ChampionList> {
            ChampionListScreen(
                onChampionClick = { championId, source ->
                    navController.navigate(
                        MainRoutes.ChampionDetail(championId, source.version, source.language)
                    )
                }
            )
        }

        // Los argumentos de la ruta los lee el ViewModel desde SavedStateHandle.
        composable<MainRoutes.ChampionDetail> {
            ChampionDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
