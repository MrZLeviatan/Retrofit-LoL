package co.edu.uniquindio.retrofit_lol.core.component.text

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.retrofit_lol.core.util.labelRes
import co.edu.uniquindio.retrofit_lol.domain.model.champion.ChampionRole

// Etiqueta pequeña con el nombre del rol (Mago, Tanque...).
@Composable
fun RoleTag(role: ChampionRole, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(role.labelRes()),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.secondary,
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}
