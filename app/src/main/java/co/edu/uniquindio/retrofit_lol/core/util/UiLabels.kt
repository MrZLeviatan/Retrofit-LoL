package co.edu.uniquindio.retrofit_lol.core.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import co.edu.uniquindio.retrofit_lol.R
import co.edu.uniquindio.retrofit_lol.domain.error.ApiException
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionRole
import java.util.Locale

// El dominio no conoce los textos: aquí se traduce cada valor a su string de strings.xml.

@StringRes
fun ChampionRole.labelRes(): Int = when (this) {
    ChampionRole.FIGHTER -> R.string.role_fighter
    ChampionRole.TANK -> R.string.role_tank
    ChampionRole.MAGE -> R.string.role_mage
    ChampionRole.ASSASSIN -> R.string.role_assassin
    ChampionRole.MARKSMAN -> R.string.role_marksman
    ChampionRole.SUPPORT -> R.string.role_support
}

// Mensaje para mostrar al usuario según el tipo de error.
@Composable
fun Throwable.toUserMessage(): String = when (this) {
    is ApiException.NoConnection -> stringResource(R.string.error_no_connection)
    is ApiException.NotFound -> stringResource(R.string.error_not_found)
    is ApiException.Server -> stringResource(R.string.error_server, code)
    else -> stringResource(R.string.error_unknown)
}

// "es_MX" -> "Español (México)".
fun languageDisplayName(code: String): String {
    val locale = Locale.forLanguageTag(code.replace('_', '-'))
    return locale.getDisplayName(locale).replaceFirstChar { it.titlecase(locale) }
}
