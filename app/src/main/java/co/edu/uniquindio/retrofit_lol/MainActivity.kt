package co.edu.uniquindio.retrofit_lol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.edu.uniquindio.retrofit_lol.core.theme.RetrofitLoLTheme
import co.edu.uniquindio.retrofit_lol.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

// @AndroidEntryPoint permite que Hilt entregue los ViewModels a las pantallas de esta actividad.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Permite que la app se dibuje detrás de la barra de estado y la de navegación del sistema
        enableEdgeToEdge()
        setContent {
            // Aplica los colores y estilos del tema a todo lo de adentro
            RetrofitLoLTheme {
                // Carga el enrutador con todas las rutas
                AppNavigation()
            }
        }
    }
}
