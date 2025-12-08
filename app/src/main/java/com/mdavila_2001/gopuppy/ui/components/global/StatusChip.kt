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

    val displayStatus = when (status.lowercase()) {
        "accepted" -> "Aceptado"
        "pending" -> "Pendiente"
        "scheduled" -> "Agendado"
        "in_progress" -> "En Curso"
        "started" -> "Iniciado"
        "finished", "completed" -> "Finalizado"
        "rejected", "cancelled" -> "Rechazado"
        "aceptado" -> "Aceptado"
        "pendiente" -> "Pendiente"
        "agendado" -> "Agendado"
        "en curso" -> "En Curso"
        "iniciado" -> "Iniciado"
        "finalizado" -> "Finalizado"
        "rechazado" -> "Rechazado"
        else -> status.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }

    val (bgColor, textColor) = when (status.lowercase()) {
        "pending", "pendiente" -> Pair(StatusPendingBg, StatusPendingText)
        "accepted", "aceptado", "scheduled", "agendado" -> Pair(StatusAcceptedBg, StatusAcceptedText)
        "rejected", "rechazado", "cancelled" -> Pair(StatusRejectedBg, StatusRejectedText)
        "in_progress", "started", "en curso", "iniciado" -> Pair(StatusInProgressBg, StatusInProgressText)
        "finished", "completed", "finalizado" -> if (isDark) {
            Pair(StatusFinishedBgDark, StatusFinishedTextDark)
        } else {
            Pair(StatusFinishedBgLight, StatusFinishedTextLight)
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
            StatusChip(status = "pending")
            Spacer(modifier = Modifier.padding(4.dp))
            StatusChip(status = "accepted")
            Spacer(modifier = Modifier.padding(4.dp))
            StatusChip(status = "scheduled")
            Spacer(modifier = Modifier.padding(4.dp))
            StatusChip(status = "in_progress")
            Spacer(modifier = Modifier.padding(4.dp))
            StatusChip(status = "started")
            Spacer(modifier = Modifier.padding(4.dp))
            StatusChip(status = "finished")
            Spacer(modifier = Modifier.padding(4.dp))
            StatusChip(status = "rejected")
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