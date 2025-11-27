package com.mdavila_2001.gopuppy.ui.components.global

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdavila_2001.gopuppy.ui.theme.*
import java.util.Locale

@Composable
fun StatusChip(
    status: String,
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()

    val displayStatus = status.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

    val (bgColor, textColor) = when (status.lowercase()) {
        "pendiente" -> Pair(StatusPendingBg, StatusPendingText)
        "aceptado" -> Pair(StatusAcceptedBg, StatusAcceptedText)
        "rechazado" -> Pair(StatusRejectedBg, StatusRejectedText)
        "en curso" -> Pair(StatusInProgressBg, StatusInProgressText)

        "finalizado" -> if (isDark) {
            Pair(StatusFinishedBgDark, StatusFinishedTextDark) // Colores Noche
        } else {
            Pair(StatusFinishedBgLight, StatusFinishedTextLight) // Colores Día
        }

        else -> Pair(Color.LightGray, Color.Black)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(50),
        modifier = modifier
    ) {
        Text(
            text = displayStatus,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatusChipLightPreview() {
    GoPuppyTheme(role = "walker", darkTheme = false) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            StatusChip(status = "Pendiente")
            Spacer(modifier = Modifier.padding(8.dp))
            StatusChip(status = "Aceptado")
            Spacer(modifier = Modifier.padding(8.dp))
            StatusChip(status = "Rechazado")
            Spacer(modifier = Modifier.padding(8.dp))
            StatusChip(status = "En curso")
            Spacer(modifier = Modifier.padding(8.dp))
            StatusChip(status = "Finalizado")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatusChipDarkPreview() {
    GoPuppyTheme(role = "owner", darkTheme = true) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            StatusChip(status = "Pendiente")
            Spacer(modifier = Modifier.padding(8.dp))
            StatusChip(status = "Aceptado")
            Spacer(modifier = Modifier.padding(8.dp))
            StatusChip(status = "Rechazado")
            Spacer(modifier = Modifier.padding(8.dp))
            StatusChip(status = "En curso")
            Spacer(modifier = Modifier.padding(8.dp))
            StatusChip(status = "Finalizado")
        }
    }
}