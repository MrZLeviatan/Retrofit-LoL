package co.edu.uniquindio.retrofit_lol.features.champion.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import co.edu.uniquindio.retrofit_lol.domain.model.catalog.GameDataSource
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionDetail
import co.edu.uniquindio.retrofit_lol.domain.usecase.champion.GetChampionDetailUseCase
import co.edu.uniquindio.retrofit_lol.domain.usecase.connectivity.ObserveConnectivityUseCase
import co.edu.uniquindio.retrofit_lol.navigation.MainRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChampionDetailUiState(
    val champion: ChampionDetail? = null,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val isOnline: Boolean = true,
)

@HiltViewModel
class ChampionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getChampionDetail: GetChampionDetailUseCase,
    private val observeConnectivity: ObserveConnectivityUseCase,
) : ViewModel() {

    // Lee los argumentos de la ruta (id, versión e idioma).
    private val route = savedStateHandle.toRoute<MainRoutes.ChampionDetail>()
    private val source = GameDataSource(route.version, route.language)

    private val _uiState = MutableStateFlow(ChampionDetailUiState())
    val uiState: StateFlow<ChampionDetailUiState> = _uiState.asStateFlow()

    init {
        load()
        viewModelScope.launch {
            observeConnectivity().collect { online ->
                val wasOffline = !_uiState.value.isOnline
                _uiState.update { it.copy(isOnline = online) }
                // Si el detalle falló por falta de red, se reintenta solo al volver la conexión.
                if (online && wasOffline && _uiState.value.error != null) load()
            }
        }
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getChampionDetail(source, route.championId)
                .onSuccess { detail -> _uiState.update { it.copy(champion = detail, isLoading = false) } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, error = error) } }
        }
    }
}
