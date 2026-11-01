package co.edu.uniquindio.retrofit_lol.features.champion.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.retrofit_lol.R
import co.edu.uniquindio.retrofit_lol.core.component.card.ChampionCard
import co.edu.uniquindio.retrofit_lol.core.component.form.AppChipRow
import co.edu.uniquindio.retrofit_lol.core.component.form.FormDropdown
import co.edu.uniquindio.retrofit_lol.core.component.form.SearchField
import co.edu.uniquindio.retrofit_lol.core.component.indicator.ConnectionIndicator
import co.edu.uniquindio.retrofit_lol.core.component.indicator.OfflineBanner
import co.edu.uniquindio.retrofit_lol.core.component.text.StatusMessage
import co.edu.uniquindio.retrofit_lol.core.util.labelRes
import co.edu.uniquindio.retrofit_lol.core.util.languageDisplayName
import co.edu.uniquindio.retrofit_lol.core.util.toUserMessage
import co.edu.uniquindio.retrofit_lol.domain.model.catalog.GameDataSource
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionRole

// Pantalla con estado: obtiene el ViewModel con Hilt y le pasa los datos a la versión sin estado.
@Composable
fun ChampionListScreen(
    onChampionClick: (championId: String, source: GameDataSource) -> Unit,
    viewModel: ChampionListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ChampionListContent(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onRoleSelected = viewModel::onRoleSelected,
        onVersionSelected = viewModel::onVersionSelected,
        onLanguageSelected = viewModel::onLanguageSelected,
        onLoadMore = viewModel::loadNextPage,
        onRetry = viewModel::retry,
        onChampionClick = { id -> state.source?.let { onChampionClick(id, it) } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChampionListContent(
    state: ChampionListUiState,
    onQueryChange: (String) -> Unit,
    onRoleSelected: (ChampionRole?) -> Unit,
    onVersionSelected: (String) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onChampionClick: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.champions_title)) },
                    actions = { ConnectionIndicator(state.isOnline, Modifier.padding(end = 12.dp)) },
                )
                OfflineBanner(state.isOnline)
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Filters(
                state = state,
                onQueryChange = onQueryChange,
                onRoleSelected = onRoleSelected,
                onVersionSelected = onVersionSelected,
                onLanguageSelected = onLanguageSelected,
            )

            // Barra delgada mientras se recarga una lista que ya tenía datos (ej. al filtrar).
            if (state.isLoading && state.champions.isNotEmpty()) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                when {
                    state.isLoading && state.champions.isEmpty() -> CircularProgressIndicator()

                    state.error != null && (state.champions.isEmpty() || state.totalResults == 0) ->
                        StatusMessage(
                            icon = Icons.Filled.CloudOff,
                            message = state.error.toUserMessage(),
                            actionLabel = stringResource(R.string.retry),
                            onAction = onRetry,
                        )

                    !state.isLoading && state.champions.isEmpty() ->
                        StatusMessage(icon = Icons.Filled.SearchOff, message = stringResource(R.string.empty_results))

                    else -> ChampionList(state, onLoadMore, onRetry, onChampionClick)
                }
            }
        }
    }
}

@Composable
private fun Filters(
    state: ChampionListUiState,
    onQueryChange: (String) -> Unit,
    onRoleSelected: (ChampionRole?) -> Unit,
    onVersionSelected: (String) -> Unit,
    onLanguageSelected: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
        // Filtro 1: texto (nombre o título del campeón).
        SearchField(
            value = state.query,
            onValueChange = onQueryChange,
            placeholder = stringResource(R.string.search_placeholder),
            clearDescription = stringResource(R.string.clear_search),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        )
        // Filtros 3 y 4: parche e idioma. Cambian la URL del endpoint de campeones.
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            FormDropdown(
                label = stringResource(R.string.filter_version),
                selected = state.selectedVersion,
                options = state.versions,
                optionLabel = { it },
                onSelected = onVersionSelected,
                modifier = Modifier.weight(1f),
            )
            FormDropdown(
                label = stringResource(R.string.filter_language),
                selected = state.selectedLanguage,
                options = state.languages,
                optionLabel = ::languageDisplayName,
                onSelected = onLanguageSelected,
                modifier = Modifier.weight(1.4f),
            )
        }
        // Filtro 2: rol (campo "tags" del JSON).
        AppChipRow(
            options = ChampionRole.entries,
            selected = state.selectedRole,
            allLabel = stringResource(R.string.role_all),
            optionLabel = { stringResource(it.labelRes()) },
            onSelected = onRoleSelected,
            contentPadding = PaddingValues(horizontal = 16.dp),
        )
        if (state.totalResults > 0) {
            Text(
                text = stringResource(R.string.results_count, state.totalResults),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
private fun ChampionList(
    state: ChampionListUiState,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onChampionClick: (String) -> Unit,
) {
    val listState = rememberLazyListState()

    // true cuando el último elemento visible está a 3 o menos del final de la lista.
    val nearEnd by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= listState.layoutInfo.totalItemsCount - PREFETCH_DISTANCE
        }
    }

    // Scroll infinito: se pide la siguiente página al acercarse al final.
    // También depende del tamaño de la lista para volver a pedir si la página nueva no llenó la pantalla.
    LaunchedEffect(nearEnd, state.champions.size) {
        if (nearEnd) onLoadMore()
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(state.champions, key = { it.id }) { champion ->
            ChampionCard(champion = champion, onClick = { onChampionClick(champion.id) })
        }
        // Pie de la lista: cargando, error de página o fin.
        item(key = "footer") {
            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                when {
                    state.isLoadingMore -> CircularProgressIndicator()
                    state.loadMoreError != null -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.error_load_more), style = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = onLoadMore) { Text(stringResource(R.string.retry)) }
                    }
                    // Error al recargar con filtros nuevos, pero hay datos viejos en pantalla.
                    state.error != null -> TextButton(onClick = onRetry) { Text(stringResource(R.string.retry)) }
                    !state.canLoadMore -> Text(
                        stringResource(R.string.end_of_list),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private const val PREFETCH_DISTANCE = 3
