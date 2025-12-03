package com.mdavila_2001.gopuppy.ui.views.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mdavila_2001.gopuppy.R
import com.mdavila_2001.gopuppy.ui.NavRoutes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val alphaAnim = remember { Animatable(0f) }
    val offsetXAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )

        delay(2000)
        
        offsetXAnim.animateTo(
            targetValue = 2000f,
            animationSpec = tween(durationMillis = 500)
        )

        navController.navigate(NavRoutes.Onboarding.route) {
            popUpTo(NavRoutes.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.gopuppy_location),
            contentDescription = "GoPuppy Logo",
            modifier = Modifier
                .size(200.dp)
                .alpha(alphaAnim.value)
                .graphicsLayer {
                    translationX = offsetXAnim.value
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.gopuppy_location),
            contentDescription = "GoPuppy Logo",
            modifier = Modifier.size(200.dp)
        )
    }
}
