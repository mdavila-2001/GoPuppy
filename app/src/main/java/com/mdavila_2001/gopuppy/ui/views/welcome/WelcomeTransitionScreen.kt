package com.mdavila_2001.gopuppy.ui.views.welcome

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mdavila_2001.gopuppy.ui.NavRoutes
import kotlinx.coroutines.delay

@Composable
fun WelcomeTransitionScreen(
    navController: NavController,
    userName: String,
    isWalker: Boolean
) {
    val alphaAnim = remember { Animatable(0f) }
    val offsetXAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Fade in del contenido
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )

        // Esperar 2 segundos
        delay(2000)
        
        // Deslizar hacia la derecha
        offsetXAnim.animateTo(
            targetValue = 2000f,
            animationSpec = tween(durationMillis = 500)
        )

        val destination = if (isWalker) NavRoutes.WalkerHome.route else NavRoutes.OwnerHome.route
        navController.navigate(destination) {
            popUpTo(NavRoutes.WelcomeTransition.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .graphicsLayer {
                    translationX = offsetXAnim.value
                }
        ) {
            Text(
                text = "¿Estás listo para",
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "otro viaje",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "$userName?",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}
