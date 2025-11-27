package com.mdavila_2001.gopuppy.ui.components.global.cards

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RatingDisplay(
    rating: Double,
    maxStars: Int = 5,
    starSize: Dp = 18.dp,
    starColor: Color = Color(0xFFFFC107),
    backgroundColor: Color = Color.LightGray
) {
    Box(
        modifier = Modifier.wrapContentWidth()
    ) {
        Row {
            repeat(maxStars) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = backgroundColor,
                    modifier = Modifier.size(starSize)
                )
            }
        }
        Row(
            modifier = Modifier
                .matchParentSize()
                .drawWithContent {
                    val widthToDraw = size.width * (rating / maxStars).toFloat()
                    clipRect(right = widthToDraw) {
                        this@drawWithContent.drawContent()
                    }
                }
        ) {
            repeat(maxStars) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = starColor,
                    modifier = Modifier.size(starSize)
                )
            }
        }
    }
}