package co.edu.uniquindio.retrofit_lol.features.champion.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.retrofit_lol.domain.model.catalog.GameDataSource
import co.edu.uniquindio.retrofit_lol.domain.model.champion.Champion
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionFilter
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionRole
import co.edu.uniquindio.retrofit_lol.domain.usecase.catalog.GetLanguagesUseCase
import co.edu.uniquindio.retrofit_lol.domain.usecase.catalog.GetVersionsUseCase
import co.edu.uniquindio.retrofit_lol.domain.usecase.champion.GetChampionsPageUseCase
import co.edu.uniquindio.retrofit_lol.domain.usecase.connectivity.ObserveConnectivityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Todo lo que la pantalla de lista necesita para dibujarse, en un solo objeto.
data class ChampionListUiState(
    val champions: List<Champion> = emptyList(),
    // Filtros locales (campos del JSON).
    val query: String = "",
    val selectedRole: ChampionRole? = null,
    // Filtros que cambian el endpoint (vienen de los endpoints de versiones e idiomas).
    val versions: List<String> = emptyList(),
    val selectedVersion: String? = null,
    val languages: List<String> = emptyList(),
    val selectedLanguage: String = GameDataSource.DEFAULT_LANGUAGE,
    // Carga de la primera página (pantalla completa).
    val isLoading: Boolean = false,
    // Carga de las páginas siguientes (pie de la lista).
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = false,
    val totalResults: Int = 0,
    // Error de la primera carga: reemplaza la lista por un mensaje con "Reintentar".
    val error: Throwable? = null,
    // Error al pedir otra página: la lista se mantiene y se muestra un botón en el pie.
    val loadMoreError: Throwable? = null,
    val isOnline: Boolean = true,
) {
    val source: GameDataSource?
        get() = selectedVersion?.let { GameDataSource(it, selectedLanguage) }
}

@HiltViewModel
class ChampionListViewModel @Inject constructor(
    private val getChampionsPage: GetChampionsPageUseCase,
    private val getVersions: GetVersionsUseCase,
    private val getLanguages: GetLanguagesUseCase,
    private val observeConnectivity: ObserveConnectivityUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChampionListUiState())
    val uiState: StateFlow<ChampionListUiState> = _uiState.asStateFlow()

    // Página que se pidió por última vez (empieza en 0).
    private var currentPage = 0
    // Petición en curso: se cancela si cambia un filtro, para no mezclar resultados.
    private var loadJob: Job? = null
    // Espera corta al escribir, para no filtrar con cada letra.
    private var searchJob: Job? = null

    init {
        observeNetwork()
        loadCatalogs()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            observeConnectivity().collect { online ->
                val wasOffline = !_uiState.value.isOnline
                _uiState.update { it.copy(isOnline = online) }
                // Al volver la conexión, se reintenta solo lo que había fallado.
                if (online && wasOffline) {
                    val state = _uiState.value
                    when {
                        state.error != null -> retry()
                        state.loadMoreError != null -> loadNextPage()
                    }
                }
            }
        }
    }

    // Pide parches e idiomas (en paralelo) y luego la primera página de campeones.
    private fun loadCatalogs() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val versions = async { getVersions() }
            val languages = async { getLanguages() }

            versions.await()
                .onSuccess { list ->
                    _uiState.update { it.copy(versions = list, selectedVersion = it.selectedVersion ?: list.firstOrNull()) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error) }
                    return@launch
                }
            // Si los idiomas fallan, se sigue con el idioma por defecto.
            languages.await().onSuccess { list -> _uiState.update { it.copy(languages = list) } }

            fetchFirstPage()
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            reload()
        }
    }

    fun onRoleSelected(role: ChampionRole?) {
        _uiState.update { it.copy(selectedRole = role) }
        reload()
    }

    fun onVersionSelected(version: String) {
        if (version == _uiState.value.selectedVersion) return
        _uiState.update { it.copy(selectedVersion = version) }
        reload()
    }

    fun onLanguageSelected(language: String) {
        if (language == _uiState.value.selectedLanguage) return
        _uiState.update { it.copy(selectedLanguage = language) }
        reload()
    }

    fun retry() {
        if (_uiState.value.versions.isEmpty()) loadCatalogs() else reload()
    }

    // Vuelve a empezar desde la página 0 con los filtros actuales.
    private fun reload() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch { fetchFirstPage() }
    }

    private suspend fun fetchFirstPage() {
        val state = _uiState.value
        val source = state.source ?: return
        _uiState.update { it.copy(isLoading = true, error = null, loadMoreError = null) }

        getChampionsPage(source, state.filter(), page = 0)
            .onSuccess { page ->
                currentPage = 0
                _uiState.update {
                    it.copy(
                        champions = page.items,
                        totalResults = page.totalResults,
                        canLoadMore = page.hasMore,
                        isLoading = false,
                    )
                }
            }
            .onFailure { error ->
                // Se conserva la lista anterior; la UI decide si mostrar el error en pantalla completa.
                _uiState.update { it.copy(isLoading = false, error = error) }
            }
    }

    // Scroll infinito: la pantalla llama esta función cuando el usuario llega cerca del final.
    fun loadNextPage() {
        val state = _uiState.value
        val source = state.source ?: return
        if (state.isLoading || state.isLoadingMore || !state.canLoadMore) return

        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true, loadMoreError = null) }
            val nextPage = currentPage + 1
            getChampionsPage(source, state.filter(), page = nextPage)
                .onSuccess { page ->
                    currentPage = nextPage
                    _uiState.update {
                        it.copy(
                            champions = it.champions + page.items,
                            canLoadMore = page.hasMore,
                            isLoadingMore = false,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoadingMore = false, loadMoreError = error) }
                }
        }
    }

    private fun ChampionListUiState.filter() = ChampionFilter(query = query, role = selectedRole)

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 300L
    }
}
