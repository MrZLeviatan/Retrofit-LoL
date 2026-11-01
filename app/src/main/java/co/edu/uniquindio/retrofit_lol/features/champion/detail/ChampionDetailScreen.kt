package co.edu.uniquindio.retrofit_lol.features.champion.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.retrofit_lol.R
import co.edu.uniquindio.retrofit_lol.core.component.indicator.ConnectionIndicator
import co.edu.uniquindio.retrofit_lol.core.component.indicator.OfflineBanner
import co.edu.uniquindio.retrofit_lol.core.component.indicator.RatingBar
import co.edu.uniquindio.retrofit_lol.core.component.text.RoleTag
import co.edu.uniquindio.retrofit_lol.core.component.text.SectionTitle
import co.edu.uniquindio.retrofit_lol.core.component.text.StatusMessage
import co.edu.uniquindio.retrofit_lol.core.theme.Abyss
import co.edu.uniquindio.retrofit_lol.core.theme.Gold
import co.edu.uniquindio.retrofit_lol.core.theme.Hextech
import co.edu.uniquindio.retrofit_lol.core.theme.Offline
import co.edu.uniquindio.retrofit_lol.core.util.toUserMessage
import co.edu.uniquindio.retrofit_lol.domain.model.champion.Ability
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionDetail
import coil3.compose.AsyncImage
import java.text.DecimalFormat

@Composable
fun ChampionDetailScreen(
    onBack: () -> Unit,
    viewModel: ChampionDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ChampionDetailContent(state = state, onBack = onBack, onRetry = viewModel::load)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChampionDetailContent(
    state: ChampionDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(state.champion?.name.orEmpty()) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                        }
                    },
                    actions = { ConnectionIndicator(state.isOnline, Modifier.padding(end = 12.dp)) },
                )
                OfflineBanner(state.isOnline)
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            val champion = state.champion
            when {
                champion != null -> DetailBody(champion)
                state.isLoading -> CircularProgressIndicator()
                state.error != null -> StatusMessage(
                    icon = Icons.Filled.CloudOff,
                    message = state.error.toUserMessage(),
                    actionLabel = stringResource(R.string.retry),
                    onAction = onRetry,
                )
            }
        }
    }
}

@Composable
private fun DetailBody(champion: ChampionDetail) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item { Header(champion) }

        item {
            Section(stringResource(R.string.detail_ratings)) {
                RatingBar(stringResource(R.string.detail_attack), champion.info.attack, Offline)
                RatingBar(stringResource(R.string.detail_defense), champion.info.defense, Color(0xFF2ECC71))
                RatingBar(stringResource(R.string.detail_magic), champion.info.magic, Hextech)
                RatingBar(stringResource(R.string.detail_difficulty), champion.info.difficulty, Gold)
            }
        }

        item {
            Section(stringResource(R.string.detail_stats)) {
                val stats = champion.stats
                StatRow(stringResource(R.string.stat_hp), stats.hp, stringResource(R.string.stat_mana), stats.mana)
                StatRow(stringResource(R.string.stat_armor), stats.armor, stringResource(R.string.stat_magic_resist), stats.magicResist)
                StatRow(stringResource(R.string.stat_attack_damage), stats.attackDamage, stringResource(R.string.stat_attack_speed), stats.attackSpeed)
                StatRow(stringResource(R.string.stat_move_speed), stats.moveSpeed, stringResource(R.string.stat_attack_range), stats.attackRange)
                if (champion.resource.isNotBlank()) {
                    Text(
                        stringResource(R.string.detail_resource, champion.resource),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        item {
            Section(stringResource(R.string.detail_abilities)) {
                (listOf(champion.passive) + champion.spells).forEach { AbilityRow(it) }
            }
        }

        if (champion.skins.isNotEmpty()) {
            item {
                SectionTitle(
                    stringResource(R.string.detail_skins, champion.skins.size),
                    Modifier.padding(horizontal = 16.dp),
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(champion.skins) { skin ->
                        Column(Modifier.width(240.dp)) {
                            AsyncImage(
                                model = skin.splashUrl,
                                contentDescription = skin.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1215f / 717f)
                                    .clip(RoundedCornerShape(8.dp)),
                            )
                            Text(skin.name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        }

        item {
            Section(stringResource(R.string.detail_lore)) {
                Text(champion.lore, style = MaterialTheme.typography.bodyMedium)
            }
        }

        if (champion.allyTips.isNotEmpty()) {
            item { TipsSection(stringResource(R.string.detail_ally_tips), champion.allyTips) }
        }
        if (champion.enemyTips.isNotEmpty()) {
            item { TipsSection(stringResource(R.string.detail_enemy_tips), champion.enemyTips) }
        }
    }
}

// Splash art con degradado y nombre encima.
@Composable
private fun Header(champion: ChampionDetail) {
    Box(Modifier.fillMaxWidth().aspectRatio(1215f / 717f)) {
        AsyncImage(
            model = champion.splashUrl,
            contentDescription = champion.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(0.4f to Color.Transparent, 1f to Abyss))
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(champion.name, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Text(champion.title.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                champion.roles.forEach { RoleTag(it) }
            }
        }
    }
}

// Tarjeta con título dorado para cada bloque del detalle.
@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(title)
            content()
        }
    }
}

@Composable
private fun StatRow(leftLabel: String, leftValue: Double, rightLabel: String, rightValue: Double) {
    Row(Modifier.fillMaxWidth()) {
        StatCell(leftLabel, leftValue, Modifier.weight(1f))
        StatCell(rightLabel, rightValue, Modifier.weight(1f))
    }
}

@Composable
private fun StatCell(label: String, value: Double, modifier: Modifier) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(STAT_FORMAT.format(value), style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun AbilityRow(ability: Ability) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box {
            AsyncImage(
                model = ability.iconUrl,
                contentDescription = ability.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp)),
            )
            // Letra de la tecla (P, Q, W, E, R) en la esquina del ícono.
            Text(
                text = ability.key,
                style = MaterialTheme.typography.labelSmall,
                color = Abyss,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(Gold, RoundedCornerShape(topStart = 4.dp))
                    .padding(horizontal = 4.dp),
            )
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(ability.name, style = MaterialTheme.typography.titleSmall)
            if (ability.cooldown.isNotBlank()) {
                Text(
                    stringResource(R.string.ability_cooldown, ability.cooldown),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            Text(ability.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun TipsSection(title: String, tips: List<String>) {
    Section(title) {
        tips.forEach { tip -> Text("• $tip", style = MaterialTheme.typography.bodySmall) }
    }
}

// 590.0 -> "590", 0.625 -> "0.625"
private val STAT_FORMAT = DecimalFormat("#.###")
