package com.mdavila_2001.gopuppy.ui.components.walker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mdavila_2001.gopuppy.ui.theme.GoPuppyTheme

@Composable
fun StatsRow(walksCount: Int, earnings: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Paseos Hoy",
            value = walksCount.toString(),
            icon = Icons.Default.DirectionsWalk
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Ganancia Est.",
            value = earnings,
            icon = Icons.Default.AttachMoney
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatsRowPreview() {
    GoPuppyTheme(role = "walker") {
        StatsRow(walksCount = 3, earnings = "$25.00")
    }
}