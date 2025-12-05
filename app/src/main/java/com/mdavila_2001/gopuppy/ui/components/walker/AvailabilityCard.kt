package com.mdavila_2001.gopuppy.ui.components.walker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mdavila_2001.gopuppy.ui.theme.GoPuppyTheme

@Composable
fun AvailabilityCard(
    isAvailable: Boolean,
    onToggle: (Boolean) -> Unit,
    isLoading: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isAvailable)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isAvailable) "Estás Disponible" else "No Disponible",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isAvailable)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isAvailable) "Tu ubicación se comparte con los dueños cercanos." else "No aparecerás en el mapa.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isAvailable)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Switch(
                checked = isAvailable,
                onCheckedChange = onToggle,
                enabled = !isLoading
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AvailabilityCardPreview() {
    GoPuppyTheme(role = "walker") {
        Column(modifier = Modifier.padding(16.dp)) {
            AvailabilityCard(
                isAvailable = true,
                onToggle = {},
                isLoading = false
            )
            AvailabilityCard(
                isAvailable = false,
                onToggle = {},
                isLoading = false
            )
        }
    }
}